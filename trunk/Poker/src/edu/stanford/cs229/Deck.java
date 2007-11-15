package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a deck(s) of cards (4)
 * 
 * @author ago
 * 
 */
public class Deck {
	private final int NUM_DECKS = 1; // Number of decks

	public final List<Card> cardsDrawn; // History of cards drawn

	Logger logger = Logger.getLogger("edu.stanford.cs229.Deck");

	public static void main(String[] args) {
		Deck deck = new Deck();
		for (int i = 0; i < 50; i++) {
			System.out.println(deck.drawCard().toString());
		}
	}

	/**
	 * Constructor
	 * 
	 */
	public Deck() {
		cardsDrawn = new ArrayList<Card>();
	}

	/**
	 * Draws a random card
	 * 
	 * @return
	 */
	public Card drawCard() {
		Card card;
		do {
			card = new Card((int) (Math.random() * 13 + 1), (int) Math
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
		logger.info("Duplicate count: " + duplicateCount);
		logger.info("Card: " + card);
		if (duplicateCount >= NUM_DECKS) {
			logger.info("Found dup");
			return false;
		} else {
			return true;
		}
	}
}
