package edu.stanford.cs229;

import java.util.List;
import java.util.logging.Logger;

/**
 * This is a base class that represents a player. The computer dealer and the
 * human player is derived from this.
 * 
 * @author ago
 * 
 */
public abstract class AbstractPlayer {

	protected Hand hand;
	private final String name;
	private int bankroll = 10000;  //initially, everyone is given $10000
	private int pot = 0;
	
	public static Logger logger = Logger.getLogger("edu.stanford.cs229.AbstractPlayer");

	/*
	public AbstractPlayer() {
		this.name="blah";
	}
	*/
	
	public AbstractPlayer(String name) {
		hand = new Hand();
		this.name = name;
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

	public abstract PlayerAction getAction(GameState state) throws ApplicationException;
	
	public List<Card> getTableCards() {
		return hand.getTableCards();
	}

	public String getName() {
		return name;
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
	
	//TODO: Make this important to call super on.  How do you do that?
	public void processEndOfGame(int resultState) {
		logger.finest("Clearing hand");
		hand = new Hand();
		pot = 0;
	}
	
	public void getBlind(int amount) {
		this.pot += amount;
	}
}
