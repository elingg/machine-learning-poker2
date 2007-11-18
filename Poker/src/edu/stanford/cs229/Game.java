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
public class Game {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	
	private final static boolean INTERACTIVE_MODE = false;
	private final static int MAX_RUNS = 10;
	private final static boolean RESTORE_PLAYERS = false;
	
	private final Deck deck;
	
	public Game() {
		deck = new Deck();
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
		
		Game game = new Game();
		
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();
		
		if(!RESTORE_PLAYERS) {
			ReinforcementLearningPlayer player1 = new ReinforcementLearningPlayer("Elizabeth");
			ReinforcementLearningPlayer player2 = new ReinforcementLearningPlayer("Alec");
			players.add(player1);
			players.add(player2);			
		} else {
			ReinforcementLearningPlayer player1 = deserializePlayer("Elizabeth");
			ReinforcementLearningPlayer player2 = deserializePlayer("Alec");
			players.add(player1);
			players.add(player2);
		}
		
		game.run(players);
	}	
	
	/**
	 * Runs the game.  Currently only 2 players are supported.
	 * Player1 is the dealer
	 * @throws ApplicationException
	 */
	public void run(List<AbstractPlayer> players) throws ApplicationException {
		AbstractPlayer player1 = players.get(0);
		AbstractPlayer player2 = players.get(1);
		
		GameState state = new GameState(players);
		boolean done = false;
		int numRuns = 0;
		
		while (!done) {
			numRuns++;
			deck.shuffleDeck();
			
			logger.info("Number of Runs: " + numRuns);
			
			logger.fine("\nStarting new game!");
			logger.fine(player1.getName() + " has $" + player1.getBankroll());
			logger.fine(player2.getName() + " has $" + player2.getBankroll());
			boolean continueGame = true;

			//Small blind and big blind
			player1.addPotByBlind(5);
			player2.addPotByBlind(5);
			
			// Step 1: "Pre-flop"
			player1.addPlayerCard(deck.drawCard());
			player1.addPlayerCard(deck.drawCard());
			player2.addPlayerCard(deck.drawCard());
			player2.addPlayerCard(deck.drawCard());
			continueGame = processBettingRound(player1, player2, state);
			if(!continueGame) {
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
			continueGame = processBettingRound(player1, player2, state);
			if(!continueGame) {
				continue;
			}
			
			// Step 3: "Turn"
			Card tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			continueGame = processBettingRound(player2, player1, state);
			if(!continueGame) {
				continue;
			}
			
			// Step 4: "River"
			tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			continueGame = processBettingRound(player2, player1, state);
			if(!continueGame) {
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
				if(Player.isDone()) {
					break;
				}
			} else {
				if(numRuns > MAX_RUNS) {
					break;
				}
			}

		}
		
		if(player1 instanceof ReinforcementLearningPlayer) {
			((ReinforcementLearningPlayer) player1).debugResults();
		}
		
		for(AbstractPlayer player : players) {
			logger.info(player.getName() + " had " + player.getBankroll());			
		}
		
		serializePlayers(players);		
	}
	
	/**
	 * Betting part 1
	 * @param player1
	 * @param player2
	 * @param state
	 * @return
	 * @throws ApplicationException
	 */
	private boolean processBettingRound(AbstractPlayer player1, AbstractPlayer player2, GameState state) throws ApplicationException {
		printTableState(player1, player2);
		PlayerAction action = player1.getAction(state);
		//state.processPlayer1Action(player1, action);
		if(action.getActionType() == ActionType.FOLD) {
			logger.fine(player1.getName() + " folds");
			processWinner(player2, player1);
			return false;
		} else if(action.getActionType() == ActionType.CHECK){
			logger.fine(player1.getName() + " checks");
			return processBettingRound2(player2, player1, state);
		} else if(action.getActionType() == ActionType.BET) {
			player1.addPotByBetting(action.getBet());
			return processBettingRound2(player2, player1, state);
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
	private boolean processBettingRound2(AbstractPlayer playerA, AbstractPlayer playerB, GameState state) throws ApplicationException {
		PlayerAction action = playerA.getAction(state);
		if(action.getActionType() == ActionType.FOLD) {
			logger.fine(playerA.getName() + " folds");
			processWinner(playerB, playerA);
			return false;
		} else if(action.getActionType() == ActionType.CHECK){
			matchBetIfNecessary(playerA, playerB);
			return true;
		} else if(action.getActionType() == ActionType.BET) {
			matchBetIfNecessary(playerA, playerB);
			playerA.addPotByBetting(action.getBet());
			return processBettingRound2(playerB, playerA, state);
		}
		//TODO refactor this:
		return false;
	}
	
	/**
	 * Player A must match Player B's bet
	 * @param playerA
	 * @param playerB
	 */
	private void matchBetIfNecessary(AbstractPlayer playerA, AbstractPlayer playerB) {
		if(playerA.getPot() < playerB.getPot()) {
			int bet = playerB.getPot() - playerA.getPot();
			playerA.addPotByChecking(bet);
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
		logger.fine("Table has: " + getCardListString(player2.getTableCards()));
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
}
