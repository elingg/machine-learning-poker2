package edu.stanford.cs229;

import java.util.List;

/**
 * Represents the card history across all games
 * @author ago
 *
 */
public class CardHistory {
	private List<Card> cards;

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public void addCard(Card card) {
		cards.add(card);
	}
}
