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
		tableCards.add(deck.drawCard());
		tableCards.add(deck.drawCard());
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();

		tableCards.add(deck.drawCard());
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		tableCards.add(deck.drawCard());
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		tableCards.add(deck.drawCard());
		printTableState(tableCards, dealer, player);
		
		dealer.getAction();
		player.getAction();
		
		
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
