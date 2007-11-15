package edu.stanford.cs229;

public class Card implements Comparable{
	private final int value;

	private final int suite;

	public Card(int value, int suite) {
		this.value = value;
		this.suite = suite;
	}

	public int getSuite() {
		return suite;
	}

	public int getValue() {
		return value;
	}
	
	public String toString() {
		return value + "," + suite;
	}
	
	public boolean equals(Card c) {
		if((c.getValue() == this.value) && (c.getSuite() == this.suite)) {
			return true;
		} else {
			return false;
		}
	}
	
	public int compareTo(Object object) {
		Card card = (Card) object;
		if(card.getValue() > this.value) {
			return -1;
		} else if(card.getValue() == this.value) {
			return 0;
		} else {
			return 1;
		}
	}

	
	
}
