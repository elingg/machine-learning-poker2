package edu.stanford.cs229;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

/**
 * Represents a game
 * @author ago
 *
 */
public class Game {
	private final static boolean INTERACTIVE_MODE = false;
	private final static int MAX_RUNS = 100000;
	
	private final Deck deck;
	
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	
	public Game() {
		deck = new Deck();
	}
	
	/**
	 * Main entrypoint into the game
	 * @param args
	 * @throws ApplicationException
	 */
	public static void main(String[] args) throws ApplicationException {
		Game game = new Game();
		//Dealer player1 = new Dealer("Dealer");
		//Player player2 = new Player("Andrew Ng");
		
		ReinforcementLearningPlayer player1 = new ReinforcementLearningPlayer("Elizabeth");
		ReinforcementLearningPlayer player2 = new ReinforcementLearningPlayer("Alec");
		game.run(player1, player2);
	}	
	
	/**
	 * Runs the game
	 * Player1 is the dealer
	 * @throws ApplicationException
	 */
	public void run(AbstractPlayer player1, AbstractPlayer player2) throws ApplicationException {
		GameState state = new GameState();
		boolean done = false;
		int numRuns = 0;
		while (!done) {
			numRuns++;
			if(numRuns % 4 == 0) {
				deck.shuffleDeck();
			}
			logger.info("Number of Runs: " + numRuns);
			
			logger.info("\nStarting new game!");
			logger.info(player1.getName() + " has $" + player1.getBankroll());
			logger.info(player2.getName() + " has $" + player2.getBankroll());
			boolean shouldContinue = true;
			
			// Step 1: "Pre-flop"
			player1.addPlayerCard(deck.drawCard());
			player1.addPlayerCard(deck.drawCard());
			player2.addPlayerCard(deck.drawCard());
			player2.addPlayerCard(deck.drawCard());
			shouldContinue = processBettingRound(player1, player2, state);
			if(!shouldContinue) {
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
			shouldContinue = processBettingRound(player1, player2, state);
			if(!shouldContinue) {
				continue;
			}
			
			// Step 3: "Turn"
			Card tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			shouldContinue = processBettingRound(player2, player1, state);
			if(!shouldContinue) {
				continue;
			}
			
			// Step 4: "River"
			tcard = deck.drawCard();
			player1.addTableCard(tcard);
			player2.addTableCard(tcard);
			shouldContinue = processBettingRound(player2, player1, state);
			if(!shouldContinue) {
				continue;
			}
			
			// Step 5: "Showdown"

			Util util = new Util();
			Hand h = util.findWinner(player1.getHand(), player2.getHand());
			if (h == null) {
				logger.info("TIE!");
				
				player1.processEndOfGame(ResultState.TIE);
				player2.processEndOfGame(ResultState.TIE);
			} else if (h.equals(player1.getHand())) {
				processWinner(player1, player2);
			} else {
				processWinner(player2, player1);
			}
			
			if (INTERACTIVE_MODE) {
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader stdin = new BufferedReader(isr);
				System.out.print("Player again? (Y/N)");
				try {
					String response = stdin.readLine();
					if ((response.indexOf("n") != -1)
							|| (response.indexOf("N") != -1)) {
						logger.info("Ending game");
						break;

					}
				} catch (IOException e) {
					e.printStackTrace();
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
			logger.info(player1.getName() + " folds");
			processWinner(player2, player1);
			return false;
		} else if(action.getActionType() == ActionType.CHECK){
			logger.info(player1.getName() + " checks");
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
			logger.info(playerA.getName() + " folds");
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
		logger.info("Entering processWinner");
		logger.info(winner.getName() + " wins!");
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
		logger.info("Table has: " + getCardListString(player2.getTableCards()));
		logger.info(player1.getName() + " has: " + player1.toString());
		logger.info(player2.getName() + " has: " + player2.toString());
	}

	/**
	 * Prints out a list of cards
	 * @param cards
	 * @return
	 */
	private static String getCardListString(List<Card> cards) {
		String s = "";
		for (Card c : cards) {
			s += "[" + c.getValue() + "," + c.getSuite() + "]";
		}
		return s;
	}
}
