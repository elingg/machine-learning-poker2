package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a deck(s) of cards
 * 
 * @author ago
 * 
 */
public class Deck {
	Logger logger = Logger.getLogger("edu.stanford.cs229.Deck");

	private final int NUM_DECKS = 1; // Number of decks

	public List<Card> cardsDrawn; // History of cards drawn

	/**
	 * Constructor
	 * 
	 */
	public Deck() {
		shuffleDeck();
	}

	/**
	 * Draws a random card
	 * 
	 * @return
	 */
	public Card drawCard() {
		Card card;
		do {
			card = new Card((int) (Math.random() * 13 + 2), (int) Math
					.floor(Math.random() * 4 + 1));
		} while (!isValid(card));
		cardsDrawn.add(card);
		return card;
	}

	/**
	 * Method to check that the randomly drawn card is valid
	 * 
	 * @param card
	 * @return
	 */
	private boolean isValid(Card card) {
		int duplicateCount = 0;
		for (Card c : cardsDrawn) {
			if (card.equals(c)) {
				duplicateCount++;
			}
		}

		if (duplicateCount >= NUM_DECKS) {
			return false;
		} else {
			return true;
		}
	}

	public void shuffleDeck() {
		cardsDrawn = new ArrayList<Card>();
	}

}
