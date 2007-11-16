package edu.stanford.cs229;

import java.util.logging.Logger;

/**
 * This is a base class that represents a player. The computer dealer and the
 * human player is derived from this.
 * 
 * @author ago
 * 
 */
public abstract class AbstractPlayer {
	protected final Hand hand;

	private int bankroll = 0;

	public static Logger logger = Logger.getLogger("edu.stanford.cs229.AbstractPlayer");

	public AbstractPlayer() {
		hand = new Hand();
	}

	public void addTableCard(Card card) {
		logger.finest("Table received card:" + card.getSuite() + ","
				+ card.getValue());
		hand.addTableCard(card);
	}

	public void addPlayerCard(Card card) {
		logger.finest("Player received card:" + card.getSuite() + ","
				+ card.getValue());
		hand.addPlayerCard(card);
	}

	public void adjustBankroll(int i) {
		bankroll += i;
	}

	public int getBankroll() {
		return bankroll;
	}

	public String toString() {
		String s = "";
		for (Card c : hand.getPlayerCards()) {
			s += "[" + c.toString() + "]";
		}
		return s;
	}
	
	public Hand getHand(){
		
		return hand;
	}

	public abstract PlayerAction getAction() throws ApplicationException;
}
