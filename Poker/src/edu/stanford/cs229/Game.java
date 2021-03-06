package edu.stanford.cs229;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a Texas Holdem Game. This is currently only for 2 player. In the
 * future, it may be extended to more players (if we have time).
 * 
 * @author alecmgo@gmail.com
 * 
 */
public class Game extends Thread {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	
	private final int BLIND_AMOUNT = 5; 
	
	private int numRuns = 0;  //number of games played
	private final int maxRuns;  //maximum number of runs
	
	private List<AbstractPlayer> players;

	private GameState gameState;

	private final Deck deck;
	
	private long lastActivityTime = System.currentTimeMillis(); 
	/**
	 * Initializes a game
	 * @param players
	 */
	public Game(List<AbstractPlayer> players) {
		this.players = players;
		this.deck = new Deck();
		this.maxRuns = Integer.MAX_VALUE;
	}
	
	/**
	 * Initializes a game.  
	 * @param players
	 * @param maxRuns Maximum number of runs.  This is used when training the bot.
	 */
	public Game(List<AbstractPlayer> players, int maxRuns) {
		this.players = players;
		this.deck = new Deck();
		this.maxRuns = maxRuns;
	}
	
	/**
	 * Initializes a game, using a fake deck. This is for testing purposes only.
	 * 
	 * @param players
	 * @param fakeDeck
	 */
	Game(List<AbstractPlayer> players, Deck fakeDeck) {
		this.players = players;
		this.deck = fakeDeck;
		this.maxRuns = Integer.MAX_VALUE;
	}
	
	/**
	 * Runs the game. Currently only 2 players are supported, but is could be
	 * possibly expanded to multiple players. 
	 * 
	 * @throws ApplicationException
	 */
	public void run() {
		try {
			AbstractPlayer player1 = players.get(0);
			AbstractPlayer player2 = players.get(1);
			
			while (true) {
				numRuns++;
				System.out.println(numRuns);
				
				//Alternate which player goes first
				if(numRuns % 2 == 0) {
					runGameRound(player2, player1);
				} else {
					runGameRound(player1, player2);
				}
				
				// Check with each player if they are done or not. This is
				// important for human players.
				boolean player1Done = player1.isDonePlaying();
				boolean player2Done = player2.isDonePlaying();
				
				//Decide whether to continue or not.
				if ((numRuns > maxRuns) || player1Done || player2Done) {
					break;
				}				
			}

			logger.info("End of game results");
			
			for (AbstractPlayer player : players) {
				logger.info(player.getName() + " : $" + player.getBankroll());
				player.debugResults();
			}

			
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs a new game round.  The first player in the array list is the dealer.
	 * @param player1
	 * @param player2
	 * @throws ApplicationException
	 */
	private void runGameRound(AbstractPlayer player1, AbstractPlayer player2) throws ApplicationException {
		lastActivityTime = System.currentTimeMillis();
		
		resetPlayerBankrollIfNecessary(players);
		
		// Clear hands before each game round. The hands cannot be cleared too
		// soon after a game round ends, or the web application will not be able
		// to reveal the cards after a showdown
		player1.clearCards();
		player2.clearCards();
		
		deck.shuffleDeck();
		
		gameState = new GameState(players);
		
		logger.finest("Round #: " + numRuns);

		logger.finest("\nStarting new game!");
		logger.finest(player1.getName() + " has $" + player1.getBankroll());
		logger.finest(player2.getName() + " has $" + player2.getBankroll());
		
		//Need a boolean to decide to continue game.  This is set to false whenever somebody folds.
		boolean continueGame = true;

		// Step 0: Blinds
		processBlind(player1, BetType.SMALL_BLIND);
		processBlind(player2, BetType.BIG_BLIND);
		
		// Step 1: "Pre-flop"
		// Changed the order in which cards are dealt. dealer = SMALL_BLIND - though this is immaterial, just to comply..
		player2.addPlayerCard(deck.drawCard());
		player1.addPlayerCard(deck.drawCard());
		player2.addPlayerCard(deck.drawCard());
		player1.addPlayerCard(deck.drawCard());
		
		continueGame = processBettingRound(player1, player2, BettingRound.PRE_FLOP);
		
		if (continueGame) {
			// Step 2: "Flop"
			Card tcard1 = deck.drawCard();
			Card tcard2 = deck.drawCard();
			Card tcard3 = deck.drawCard();
			player1.addTableCard(tcard1);
			player1.addTableCard(tcard2);
			player1.addTableCard(tcard3);
			player2.addTableCard(tcard1);
			player2.addTableCard(tcard2);
			player2.addTableCard(tcard3);
			continueGame = processBettingRound(player2, player1, BettingRound.FLOP);
		}

		Card tcard;
		if (continueGame) {
			// Step 3: "Turn"
			tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			continueGame = processBettingRound(player2, player1, BettingRound.TURN);
		}

		if (continueGame) {
			// Step 4: "River"
			tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			continueGame = processBettingRound(player2, player1, BettingRound.RIVER);
		}

		if (continueGame) {
			// Step 5: "Showdown"
			Hand h = Util.findWinner(player1.getHand(), player2.getHand());
			if (h == null) {
				processTie(players);
			} else if (h.equals(player1.getHand())) {
				processWinner(player1, player2);
			} else {
				processWinner(player2, player1);
			}
		}
		
	}
	
	/**
	 * Process a betting round. The intuition with this is that there are only a
	 * few possible state transition diagrams:
	 * <ul>
	 * <li>P1 Checks -> P2 Checks -> End</li>
	 * <li>P1 Checks -> P2 Bets -> P1 Raises -> ... keep raises until somebody
	 * Calls -> End</li>
	 * <li>P1 Bets -> P2 Calls -> End</li>
	 * <li>P1 Bets -> P2 Raises -> P1 Raises -> ... keep raisng until somebody
	 * Calls -> End</li>
	 * <li>P1 or P2 Folding at any time leads to an end</li>
	 * </ul>
	 * Here are a few facts:
	 * <ul>
	 * <li>Folding at any time leads to the end of a round</li>
	 * <li>If somebody calls (and they are not the first person to start a
	 * betting round), this ends the round</li>
	 * <li>The round will continue forever if they keep betting/raising each
	 * other</li>
	 * </ul>
	 * 
	 * @param player1
	 * @param player2
	 * @param phase of the round (Blinds, Pre-Flop, Flop, Turn, River) used for recording purposes
	 * @return
	 * @throws ApplicationException
	 */
	private boolean processBettingRound(AbstractPlayer player1, AbstractPlayer player2, int phase) throws ApplicationException {
		printTableState(player1, player2);
		Boolean b = processAction(player1, player2, phase);
		
		//If player1 folds right away, then the betting round ends.  
		//if((b != null) && (b != true)) {
		if(b == Boolean.FALSE) {
			return false;
		}
	
		int turnCount = 0;
		while (true) {
			if (turnCount % 2 == 0) {
				Boolean continueGame = processAction(player2, player1, phase);
				if(continueGame != null) {
					return continueGame;
				}
			} else {
				Boolean continueGame = processAction(player1, player2, phase);
				if(continueGame != null) {
					return continueGame;
				}
			}
			turnCount++;
		}		
	}

	/**
	 * Process an action for an individual player.
	 * 
	 * @param player1
	 * @param player2
	 * @param phase
	 *            of the round (Blinds, Pre-Flop, Flop, Turn, River) used for
	 *            recording purposes
	 * @return 3 possible states are returned:
	 *         <ul>
	 *         <li>true if the betting should end, but the game round should
	 *         continue. This is always the case with checking/calling</li>
	 *         <li>false if the betting should end, and the game round should
	 *         end. This is always the case with folding</li>
	 *         <li>null if the betting should continue. This is always the case
	 *         with betting/raising</li>
	 *         </ul>
	 * 
	 * @throws ApplicationException
	 */
	private Boolean processAction(AbstractPlayer player1, AbstractPlayer player2, int phase) throws ApplicationException {
		PlayerAction action1 = player1.getAction(gameState);

		if (action1.getActionType() == ActionType.FOLD) {
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(player1, numRuns, phase, action1));
			processWinner(player2, player1);
			return Boolean.FALSE;
		}
		if (action1.getActionType() == ActionType.CHECK_OR_CALL) {
			if(player1.getPot() < player2.getPot()) {
				int match = player2.getPot() - player1.getPot();
				action1.overrideBet(match);
				player1.addPot(match);
			}
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(player1, numRuns, phase, action1));
			return Boolean.TRUE;
		}
		if (action1.getActionType() == ActionType.BET_OR_RAISE) {
			boolean isRaise = false;  //determines if this is a bet or raise
			if(player1.getPot() < player2.getPot()) {
				int match = player2.getPot() - player1.getPot();
				player1.addPot(match);
				PlayerAction action = new PlayerAction(ActionType.CHECK_OR_CALL, match);
				gameState.addPlayerActivityRecord(new PlayerActivityRecord(player1, numRuns, phase, action));
				isRaise = true;
			}
			
			if(isRaise) {
				action1.setBetType(BetType.RAISE);
			} else {
				action1.setBetType(BetType.BET);
			}		
			
			player1.addPot(action1.getBet());
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(player1, numRuns, phase, action1));
			return null;
		}
		throw new IllegalArgumentException("FATAL EXCEPTION: Player action was invalid");
	}
	
	/**
	 * Processes a blind for a player.  This can handle the big blind and the small blind.
	 * @param player
	 * @param betType
	 */
	private void processBlind(AbstractPlayer player, BetType betType) {
		if(betType.equals(BetType.SMALL_BLIND)) {
			player.addPot(BLIND_AMOUNT);
			
			PlayerAction action = new PlayerAction(ActionType.BET_OR_RAISE, BLIND_AMOUNT);
			action.setBetType(betType);			
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(player, numRuns, 0, action));
			
		} else if(betType.equals(BetType.BIG_BLIND)){
			player.addPot(BLIND_AMOUNT * 2);
			
			PlayerAction action = new PlayerAction(ActionType.BET_OR_RAISE, BLIND_AMOUNT * 2);
			action.setBetType(betType);
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(player, numRuns, 0, action));
		} else {
			throw new IllegalArgumentException("Invalid blind type");
		}
	}
	
	/**
	 * Process the winner. This adjusts the bankroll for the winner and loser,
	 * and creates a record.
	 * 
	 * @param winner
	 * @param loser
	 */
	private void processWinner(AbstractPlayer winner, AbstractPlayer loser) {
		gameState.addPlayerActivityRecord(new PlayerActivityRecord(winner, numRuns, BettingRound.SHOWDOWN, ResultState.WIN));
		gameState.addPlayerActivityRecord(new PlayerActivityRecord(loser, numRuns, BettingRound.SHOWDOWN, ResultState.LOSE));
		winner.adjustBankroll(loser.getPot());
		loser.adjustBankroll(-1 * loser.getPot());
		winner.processEndOfRound(ResultState.WIN);
		loser.processEndOfRound(ResultState.LOSE);
	}
	
	/**
	 * Processes a tie
	 * @param players
	 */
	private void processTie(List<AbstractPlayer> players) {
		for(AbstractPlayer p : players) {
			p.processEndOfRound(ResultState.TIE);
			gameState.addPlayerActivityRecord(new PlayerActivityRecord(p, numRuns, BettingRound.SHOWDOWN, ResultState.TIE));
		}		
	}

	/**
	 * Prints the cards of all the players
	 * @param tableCards
	 * @param player1
	 * @param player2
	 */
	private static void printTableState(AbstractPlayer player1, AbstractPlayer player2) {
		logger.finest("Table has: " + getCardListString(player2.getHand().getTableCards()));
		logger.finest(player1.getName() + " has: " + player1.toString());
		logger.finest(player2.getName() + " has: " + player2.toString());
	}

	/**
	 * Returns a string for a list of cards
	 * @param cards
	 * @return
	 */
	private static String getCardListString(List<Card> cards) {
		String s = "";
		for (Card c : cards) {
			s += "[" + c.getValue() + "," + c.getSuit() + "]";
		}
		return s;
	}
	
	/**
	 * Returns game state
	 * @return
	 */
	public GameState getGameState() {
		return gameState;
	}
	
	/**
	 * Resets the bankroll if necessary.  If a player gets negative money, then each player's
	 * bankroll is reset to the default value.  
	 * @param players
	 */
	private void resetPlayerBankrollIfNecessary(List<AbstractPlayer> players) {
		for(AbstractPlayer p : players) {
			if(p.getBankroll() <= 0) {
				for(AbstractPlayer q : players) {
					q.resetBankroll();
				}
				return;
			}
		}
	}
	

	public static AbstractPlayer generatePlayer(String className,
			String playerName) throws ApplicationException {
		try {
			Class object = Class.forName(className);
			Constructor c = object.getConstructor(new Class[] { String.class });
			return (AbstractPlayer) c.newInstance(new Object[] {playerName});
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		} catch (NoSuchMethodException e) {
			throw new ApplicationException(e);
		} catch (IllegalAccessException e) {
			throw new ApplicationException(e);
		} catch (InstantiationException e) {
			throw new ApplicationException(e);
		} catch (InvocationTargetException e) {
			throw new ApplicationException(e);
		}
	}

	public long getLastActivityTime() {
		return lastActivityTime;
	}


}
