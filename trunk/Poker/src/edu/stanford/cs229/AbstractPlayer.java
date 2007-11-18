package edu.stanford.cs229;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is a base class that represents a player. The computer dealer and the
 * human player is derived from this.
 * 
 * @author ago
 * 
 */
public abstract class AbstractPlayer implements Serializable {
	public static Logger logger = Logger
			.getLogger("edu.stanford.cs229.AbstractPlayer");

	private final String name;

	protected Hand hand;

	private int bankroll = 10000; // initially, everyone is given $10000

	private int pot = 0; //how much the player has put into the current round

	public AbstractPlayer(String name) {
		hand = new Hand();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Hand getHand() {
		return hand;
	}

	public int getPot() {
		return pot;
	}

	public void addPotByBetting(int pot) {
		logger.fine(name + " bets $" + pot);
		this.pot += pot;
	}

	public void addPotByChecking(int pot) {
		logger.fine(name + " checks $" + pot);
		this.pot += pot;
	}

	public void addPotByBlind(int amount) {
		this.pot += amount;
	}

	public int getBankroll() {
		return bankroll;
	}

	public void adjustBankroll(int i) {
		bankroll += i;
	}

	public List<Card> getTableCards() {
		return hand.getTableCards();
	}

	public void addTableCard(Card card) {
		logger.finest("Table received card:" + card.getSuit() + ","
				+ card.getValue());
		hand.addTableCard(card);
	}

	public void addPlayerCard(Card card) {
		logger.finest("Player received card:" + card.getSuit() + ","
				+ card.getValue());
		hand.addPlayerCard(card);
	}

	// TODO: Make this important to call super on. How do you do that?
	public void processEndOfGame(int resultState) {
		hand = new Hand();
		pot = 0;
	}

	public String toString() {
		String s = "";
		for (Card c : hand.getPlayerCards()) {
			s += "[" + c.toString() + "]";
		}
		return s;
	}

	public abstract PlayerAction getAction(GameState state)
			throws ApplicationException;
	
}
