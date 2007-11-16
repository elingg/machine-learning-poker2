package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author ago
 *
 */
public class GameState {
	private final List<Card> tableCards;

	private final Deck deck;

	/**
	 * Constructor
	 *
	 */
	public GameState() {
		tableCards = new ArrayList<Card>();
		deck = new Deck();
	}

	/**
	 * Runs the game
	 * @throws ApplicationException
	 */
	public void run() throws ApplicationException {
		Player player = new Player();
		Dealer dealer = new Dealer();

		System.out.println("Starting game!");

		//Assign cards to player
		player.addPlayerCard(deck.drawCard());
		player.addPlayerCard(deck.drawCard());

		//Assign cards to dealer
		dealer.addPlayerCard(deck.drawCard());
		dealer.addPlayerCard(deck.drawCard());

		//Assign cards to table
		Card tcard1=deck.drawCard();
		Card tcard2=deck.drawCard();
		tableCards.add(tcard1);
		tableCards.add(tcard2);
		player.addTableCard(tcard1);
		player.addTableCard(tcard2);
		dealer.addTableCard(tcard1);
		dealer.addTableCard(tcard2);
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();

		tableCards.add(deck.drawCard());
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		Card tcard=deck.drawCard();
		tableCards.add(tcard);
		player.addTableCard(tcard);
		dealer.addTableCard(tcard);
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		tcard=deck.drawCard();
		tableCards.add(tcard);
		player.addTableCard(tcard);
		dealer.addTableCard(tcard);
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		Util util= new Util();
		Hand h= util.findWinner(player.getHand(), dealer.getHand());
		if(h==null)
			System.out.println("TIE!");
		else if(h.equals(player.getHand()))
				System.out.println("Player wins!");
		else
			System.out.println("Dealer wins!");
	
		
	}
	

	/**
	 * Prints the cards of all the players
	 * @param tableCards
	 * @param dealer
	 * @param player
	 */
	private static void printTableState(List<Card> tableCards, Dealer dealer, Player player) {
		System.out.println("Table has: " + getCardListString(tableCards));
		System.out.println("Dealer has: " + dealer.toString());
		System.out.println("You have: " + player.toString());
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
