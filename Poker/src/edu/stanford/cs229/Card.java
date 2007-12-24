package edu.stanford.cs229;

import java.io.Serializable;

/**
 * Represents a card, which consists of a value and a suit
 * @author ago
 *
 */
public class Card implements Comparable, Serializable {
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
		return value + ";" + suit;
	}

	public boolean equals(Card c) {
		if ((c.getValue() == this.value) && (c.getSuit() == this.suit)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the card as an image tag.  This is used for the web application.
	 * @return
	 */
	public String toHtmlString() {
		int suit = this.getSuit();
		char suitChar;
		switch (suit) {
		case 1:
			suitChar = 'c';
			break;
		case 2:
			suitChar = 'd';
			break;
		case 3:
			suitChar = 'h';
			break;
		case 4:
			suitChar = 's';
			break;
		default:
			suitChar = 'X';
			break;
		}

		String valueString;
		int value = this.getValue();
		switch (value) {
		case 11:
			valueString = "j";
			break;
		case 12:
			valueString = "q";
			break;
		case 13:
			valueString = "k";
			break;
		case 14:
			valueString = "a";
			break;
		default:
			valueString = Integer.toString(value);
		}
		String imgLink = "<img src=\"http://www.csse.monash.edu.au/~stevenm/texasholdem/images/ornamental_" + suitChar + "_"
				+ valueString + "_small.png\">";
		return imgLink;
	}
	
	/**
	 * Returns a placeholder card image.  This is for the web application.
	 * @return
	 */
	public static String getTableCardPlaceHolderAsHtml() {
		return "<img src=\"http://www.csse.monash.edu.au/~stevenm/texasholdem/images/cardspot_small.png\"/>";
	}
	
	/**
	 * Returns a placeholder card image.  This is for the web application.
	 * @return
	 */
	public static String getHiddenCardsAsHtml() {
		String s = "";
		for(int i = 0; i < 2; i++) {
		  s += "<img src=\"http://www.csse.monash.edu.au/~stevenm/texasholdem/images/back07_small.png\"/>";
		}		
		return s;
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
