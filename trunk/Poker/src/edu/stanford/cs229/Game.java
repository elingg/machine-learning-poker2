package edu.stanford.cs229;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

/**
 * Represents a Texas Hold'em Game. This is currently only for 2 player. In the
 * future, it may be extended to 3 players.
 * 
 * @author ago
 * 
 */
public class Game extends Thread {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	
	//Interactive mode
	private final static boolean INTERACTIVE_MODE = false;

	//Load the player from disk?
	private final static boolean RESTORE_PLAYERS = false;  
	
	//Number of games to be played
	private final static int MAX_RUNS = 10;
	
	private int numRuns = 0; // number of rounds to be played

	private List<AbstractPlayer> players;

	private GameState gameState;

	private final Deck deck = new Deck();

	public Game(List<AbstractPlayer> players) {
		this.players = players;
	}
	
	/**
	 * Main entry-point into the game, if running through the command-line
	 * @param args
	 * @throws ApplicationException
	 */
	public static void main(String[] args) throws ApplicationException {
		//Set up the logging config
		//TODO: The following doesn't work.
		System.setProperty("java.util.logging.config.file", "logging.properties");
		
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();
		
		if(INTERACTIVE_MODE) {
			RandomPlayer dealer = new RandomPlayer("Andrew");
			HumanPlayer player = new HumanPlayer("Alec");
			players.add(dealer);
			players.add(player);
		} else if(RESTORE_PLAYERS) {
			ReinforcementLearningPlayer player1 = deserializePlayer("Elizabeth");
			ReinforcementLearningPlayer player2 = deserializePlayer("Alec");
			players.add(player1);
			players.add(player2);
		} else {
			ReinforcementLearningPlayer player1 = new ReinforcementLearningPlayer("Elizabeth");
			ReinforcementLearningPlayer player2 = new ReinforcementLearningPlayer("Alec");
			players.add(player1);
			players.add(player2);
		}
		
		Game game = new Game(players);
		game.run();
	}	
	
	/**
	 * Runs the game. Currently only 2 players are supported, but is could be
	 * possibly expanded to multiple players. The first player in the array list
	 * is the dealer.
	 * 
	 * @throws ApplicationException
	 */
	public void run() {
		try {
			AbstractPlayer player1 = players.get(0);
			AbstractPlayer player2 = players.get(1);

			while (true) {
				numRuns++;
				deck.shuffleDeck();
				gameState = new GameState(players);
				
				logger.info("Round #: " + numRuns);

				logger.fine("\nStarting new game!");
				logger.fine(player1.getName() + " has $" + player1.getBankroll());
				logger.fine(player2.getName() + " has $" + player2.getBankroll());
				
				//Need a boolean to decide to continue game.  This is set to false whenever somebody folds.
				boolean continueGame = true;

				// Step 0: Blinds
				player1.addPotByBlind(5);
				player2.addPotByBlind(5);

				// Step 1: "Pre-flop"
				player1.addPlayerCard(deck.drawCard());
				player1.addPlayerCard(deck.drawCard());
				player2.addPlayerCard(deck.drawCard());
				player2.addPlayerCard(deck.drawCard());
				continueGame = processBettingRound(player1, player2, 1);

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
					continueGame = processBettingRound(player1, player2, 2);
				}

				Card tcard;
				if (continueGame) {
					// Step 3: "Turn"
					tcard = deck.drawCard();
					player1.addTableCard(tcard);
					player2.addTableCard(tcard);
					continueGame = processBettingRound(player2, player1, 3);
				}

				if (continueGame) {
					// Step 4: "River"
					tcard = deck.drawCard();
					player1.addTableCard(tcard);
					player2.addTableCard(tcard);
					continueGame = processBettingRound(player2, player1, 4);
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

				//Check with each player if they are done or not.  This is important for humann players.
				boolean player1Done = player1.isDonePlaying();
				boolean player2Done = player2.isDonePlaying();
				
				//Decide whether to continue or not.
				if ((numRuns > MAX_RUNS) || player1Done || player2Done) {
					break;
				}
				
				// Clear hands need to be called after isDonePlaying().
				// Otherwise, the cards will not appear correctly in the webapp
				player1.clearCards();
				player2.clearCards();
			}

			logger.info("End of game results");
			
			for (AbstractPlayer player : players) {
				logger.info(player.getName() + " : $" + player.getBankroll());
				player.debugResults();
			}

			serializePlayers(players);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Process a betting round. The intuitoin with this is that there are only a
	 * few possible state transition diagrams:
	 * <ul>
	 * <li>P1 Checks -> P2 Checks -> End</li>
	 * <li>P1 Checks -> P2 Bets -> P1 Raises -> ... keep raises until somebody
	 * Calls -> End</li>
	 * <li>P1 Bets -> P2 Calls -> End</li>
	 * <li>P1 Bets -> P2 Raises -> P1 Raises -> ... keep rasing until somebody
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
		Boolean b = processBet(player1, player2, phase);
		
		//If player1 folds right away, then the betting round ends.  
		//if((b != null) && (b != true)) {
		if(b == Boolean.FALSE) {
			return false;
		}
	
		int turnCount = 0;
		while (true) {
			if (turnCount % 2 == 0) {
				Boolean continueGame = processBet(player2, player1, phase);
				if(continueGame != null) {
					return continueGame;
				}
			} else {
				Boolean continueGame = processBet(player1, player2, phase);
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
	private Boolean processBet(AbstractPlayer player1, AbstractPlayer player2, int phase) throws ApplicationException {
		PlayerAction action1 = player1.getAction(gameState);
		if (action1.getActionType() == ActionType.FOLD) {
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action1));
			processWinner(player2, player1);
			return Boolean.FALSE;
		}
		if (action1.getActionType() == ActionType.CHECK_OR_CALL) {
			if(player1.getPot() < player2.getPot()) {
				int match = player2.getPot() - player1.getPot();
				action1.overrideBet(match);
				player1.addPotByCalling(match);
			}
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action1));
			return Boolean.TRUE;
		}
		if (action1.getActionType() == ActionType.BET_OR_RAISE) {
			if(player1.getPot() < player2.getPot()) {
				int match = player2.getPot() - player1.getPot();
				player1.addPotByCalling(match);
				PlayerAction action = new PlayerAction(ActionType.CHECK_OR_CALL, match);
				gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
			}
			player1.addPotByBetting(action1.getBet());
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action1));
			return null;
		}
		throw new RuntimeException("FATAL EXCEPTION: Player action was invalid");
	}
	
	/**
	 * Processes a betting around
	 * @param player1
	 * @param player2
	 * @param state
	 * @param isLastPerson indicates if this is the last person in the betting round, if they choose to check or call
	 * @return Indicates if the game should continue.  True if yes, false if no (i.e. somebody folded).
	 * @throws ApplicationException
	 * @deprecated This was a weird/hard to debug recursive function.
	 */
	/*
	private boolean processBettingRound(AbstractPlayer player1, AbstractPlayer player2, int phase, boolean isLastPerson) throws ApplicationException {
		if(!isLastPerson) {
			printTableState(player1, player2);
		}
		
		PlayerAction action = player1.getAction(gameState);
		
		if(action.getActionType() == ActionType.FOLD) {
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
			processWinner(player2, player1);
			return false;
		}		

		if(action.getActionType() == ActionType.CHECK_OR_CALL){
			//If there is a difference between the player pots, then this is a call.  Otherwise, it is a check.
			if(player1.getPot() < player2.getPot()) {
				int bet = player2.getPot() - player1.getPot();
				action.overrideBet(bet);	
				gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
				player1.addPotByCalling(bet);
			} else {
				gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
			}
			
			if(!isLastPerson) {
				return processBettingRound(player2, player1, phase, true);
			} else {				
				return true;
			}
		} 
		
		if(action.getActionType() == ActionType.BET_OR_RAISE) {
			//If there is a different between the player pots, then they must call first.  Then they can raise.
			if(player1.getPot() < player2.getPot()) {
				int bet = player2.getPot() - player1.getPot();
				PlayerAction call = new PlayerAction(ActionType.CHECK_OR_CALL, bet);
				gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, call));
				player1.addPotByCalling(bet);
			} 
			
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
			player1.addPotByBetting(action.getBet());
			return processBettingRound(player2, player1, phase, true);
		}
		
		throw new RuntimeException("PLAYER ENTERED BETTING ROUND WITHOUT AN ACTION");
	}
	*/
	
	
	/**
	 * Process the winner
	 * @param winner
	 * @param loser
	 */
	private void processWinner(AbstractPlayer winner, AbstractPlayer loser) {
		gameState.addPlayerActivityRecord(new PlayerActvityRecord(winner.getId(), winner.getName(), numRuns, 5, ResultState.WIN));
		gameState.addPlayerActivityRecord(new PlayerActvityRecord(loser.getId(), loser.getName(), numRuns, 5, ResultState.LOSE));
		winner.adjustBankroll(loser.getPot());
		loser.adjustBankroll(-1 * loser.getPot());
		winner.processEndOfGame(ResultState.WIN);
		loser.processEndOfGame(ResultState.LOSE);
	}
	
	/**
	 * Processes a tie
	 * @param players
	 */
	private void processTie(List<AbstractPlayer> players) {
		for(AbstractPlayer p : players) {
			p.processEndOfGame(ResultState.TIE);
			gameState.addPlayerActivityRecord(new PlayerActvityRecord(p.getId(), p.getName(), numRuns, 5, ResultState.TIE));
		}		
	}

	/**
	 * Prints the cards of all the players
	 * @param tableCards
	 * @param player1
	 * @param player2
	 */
	private static void printTableState(AbstractPlayer player1, AbstractPlayer player2) {
		logger.fine("Table has: " + getCardListString(player2.getHand().getTableCards()));
		logger.fine(player1.getName() + " has: " + player1.toString());
		logger.fine(player2.getName() + " has: " + player2.toString());
	}

	/**
	 * Prints out a list of cards
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
	 * Serializes a ReinforcementLearnerPlayer
	 * @param players
	 * @throws ApplicationException
	 */
	private static void serializePlayers(List<AbstractPlayer> players) throws ApplicationException {
		try {
			for (AbstractPlayer player : players) {
				ObjectOutputStream os = new ObjectOutputStream(
						new FileOutputStream(player.getName()));
				os.writeObject(player);
				os.close();
			}
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}
	
	/**
	 * Deserializes a ReinforcementLearnerPlayer
	 * @param name
	 * @return
	 * @throws ApplicationException
	 */
	private static ReinforcementLearningPlayer deserializePlayer(String name) throws ApplicationException {
		ObjectInput input = null;
		try {
	      InputStream file = new FileInputStream(name);
	      InputStream buffer = new BufferedInputStream( file );
	      input = new ObjectInputStream ( buffer );
	      //deserialize the List
	      ReinforcementLearningPlayer player = (ReinforcementLearningPlayer) input.readObject();
	      return player;
		} catch(IOException e) {
			throw new ApplicationException(e);
		} catch(ClassNotFoundException e) {
			throw new ApplicationException(e);
		}		
	}

	public GameState getGameState() {
		return gameState;
	}
}
