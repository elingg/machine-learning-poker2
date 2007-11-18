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
 * Represents a game
 * @author ago
 *
 */
public class Game extends Thread {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	
	//Interactive mode
	private final static boolean INTERACTIVE_MODE = false;
	
	//Number of games to be played
	private final static int MAX_RUNS = 10000000;
	
	//Load the player from disk?
	private final static boolean RESTORE_PLAYERS = false;  
	
	private int numRuns = 0;
	private final Deck deck;
	private List<AbstractPlayer> players;
	private GameState gameState;
	
	public Game(List<AbstractPlayer> players) {
		this.deck = new Deck();
		this.players = players;
	}
	
	/**
	 * Main entrypoint into the game
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
	 * Runs the game.  Currently only 2 players are supported, but is could be possibly expanded to multiple players.
	 * The first player in the array list is the dealer.  
	 * @throws ApplicationException
	 */
	public void run() {
		try {
			AbstractPlayer player1 = players.get(0);
			AbstractPlayer player2 = players.get(1);
			
			boolean done = false;

			while (!done) {
				numRuns++;
				deck.shuffleDeck();
				gameState = new GameState(players);
				
				logger.info("Number of Runs: " + numRuns);

				logger.fine("\nStarting new game!");
				logger.fine(player1.getName() + " has $"
						+ player1.getBankroll());
				logger.fine(player2.getName() + " has $"
						+ player2.getBankroll());
				boolean continueGame = true;

				// Small blind and big blind
				player1.addPotByBlind(5);
				player2.addPotByBlind(5);

				// Step 1: "Pre-flop"
				player1.addPlayerCard(deck.drawCard());
				player1.addPlayerCard(deck.drawCard());
				player2.addPlayerCard(deck.drawCard());
				player2.addPlayerCard(deck.drawCard());
				continueGame = processBettingRound(player1, player2, 1, false);
				if (!continueGame) {
					continue;
				}

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
				continueGame = processBettingRound(player1, player2, 2, false);
				if (!continueGame) {
					continue;
				}

				// Step 3: "Turn"
				Card tcard = deck.drawCard();
				player1.addTableCard(tcard);
				player2.addTableCard(tcard);
				continueGame = processBettingRound(player2, player1, 3, false);
				if (!continueGame) {
					continue;
				}

				// Step 4: "River"
				tcard = deck.drawCard();
				player1.addTableCard(tcard);
				player2.addTableCard(tcard);
				continueGame = processBettingRound(player2, player1, 4, false);
				if (!continueGame) {
					continue;
				}

				// Step 5: "Showdown"
				Hand h = Util.findWinner(player1.getHand(), player2.getHand());
				if (h == null) {
					logger.fine("TIE!");
					player1.processEndOfGame(ResultState.TIE);
					player2.processEndOfGame(ResultState.TIE);
				} else if (h.equals(player1.getHand())) {
					processWinner(player1, player2);
				} else {
					processWinner(player2, player1);
				}

				if (INTERACTIVE_MODE) {
					if (HumanPlayer.isDone()) {
						break;
					}
				} else {
					if (numRuns > MAX_RUNS) {
						break;
					}
				}

			}

			if (player1 instanceof ReinforcementLearningPlayer) {
				((ReinforcementLearningPlayer) player1).debugResults();
			}

			for (AbstractPlayer player : players) {
				logger.info(player.getName() + " had " + player.getBankroll());
			}

			serializePlayers(players);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes a betting around
	 * @param player1
	 * @param player2
	 * @param state
	 * @param isLastPerson indicates if this is the last person in the betting round, if they choose to check or call
	 * @return Indicates if the game should continue.  True if yes, false if no (i.e. somebody folded).
	 * @throws ApplicationException
	 */
	private boolean processBettingRound(AbstractPlayer player1, AbstractPlayer player2, int phase, boolean isLastPerson) throws ApplicationException {
		printTableState(player1, player2);
		PlayerAction action = player1.getAction(gameState);
		
		//TODO: Fill details in later
		gameState.addPlayerActionRecord(new PlayerActvityRecord(player1.getId(), player1.getName(), numRuns, phase, action));
		
		if(action.getActionType() == ActionType.FOLD) {
			logger.fine(player1.getName() + " folds");
			processWinner(player2, player1);
			return false;
		} else if(action.getActionType() == ActionType.CHECK_OR_CALL){
			if(!isLastPerson) {
				logger.fine(player1.getName() + " checks");
				return processBettingRound(player2, player1, phase, true);
			} else {
				matchBetIfNecessary(player1, player2);
				return true;
			}
		} else if(action.getActionType() == ActionType.BET_OR_RAISE) {
			if(isLastPerson) {
				matchBetIfNecessary(player1, player2);
			}
			player1.addPotByBetting(action.getBet());
			return processBettingRound(player2, player1, phase, true);
		}
		//TODO refactor this:
		return false;
	}
	
	/**
	 * Betting part 2
	 * @param playerA
	 * @param playerB
	 * @param state
	 * @return
	 * @throws ApplicationException
	 * TODO: There is a lot of redundant code between this and processBettingRound
	 */
	/*
	private boolean processBettingRound2(AbstractPlayer playerA, AbstractPlayer playerB, int phase) throws ApplicationException {
		PlayerAction action = playerA.getAction(gameState);
		if(action.getActionType() == ActionType.FOLD) {
			logger.fine(playerA.getName() + " folds");
			processWinner(playerB, playerA);
			return false;
		} else if(action.getActionType() == ActionType.CHECK_CALL){
			matchBetIfNecessary(playerA, playerB); //diff
			return true;  //diff
		} else if(action.getActionType() == ActionType.BET_RAISE) {
			matchBetIfNecessary(playerA, playerB);  //diff
			playerA.addPotByBetting(action.getBet());
			return processBettingRound2(playerB, playerA, phase);
		}
		//TODO refactor this:
		return false;
	}
	*/
	
	/**
	 * Player A must match Player B's bet
	 * @param playerA
	 * @param playerB
	 */
	private void matchBetIfNecessary(AbstractPlayer playerA, AbstractPlayer playerB) {
		if(playerA.getPot() < playerB.getPot()) {
			int bet = playerB.getPot() - playerA.getPot();
			playerA.addPotByCalling(bet);
		}
	}
	
	/**
	 * Process the winner
	 * @param winner
	 * @param loser
	 */
	private void processWinner(AbstractPlayer winner, AbstractPlayer loser) {
		logger.fine("Entering processWinner");
		logger.fine(winner.getName() + " wins!");
		gameState.addPlayerActionRecord(new PlayerActvityRecord(winner.getId(), winner.getName(), numRuns, 0, ResultState.WIN));
		winner.adjustBankroll(loser.getPot());
		loser.adjustBankroll(-1 * loser.getPot());
		
		winner.processEndOfGame(ResultState.WIN);
		loser.processEndOfGame(ResultState.LOSE);
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
