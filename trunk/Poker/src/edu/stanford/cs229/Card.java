package edu.stanford.cs229;

/**
 * Represents a card, which consists of a value and a suit
 * @author ago
 *
 */
public class Card implements Comparable {
	private final int value;

	private final int suit;

	public Card(int value, int suit) {
		this.value = value;
		this.suit = suit;
	}

	public int getSuit() {
		return suit;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return value + "," + suit;
	}

	public boolean equals(Card c) {
		if ((c.getValue() == this.value) && (c.getSuit() == this.suit)) {
			return true;
		} else {
			return false;
		}
	}

	public int compareTo(Object object) {
		Card card = (Card) object;
		if (card.getValue() > this.value) {
			return -1;
		} else if (card.getValue() == this.value) {
			return 0;
		} else {
			return 1;
		}
	}
}
