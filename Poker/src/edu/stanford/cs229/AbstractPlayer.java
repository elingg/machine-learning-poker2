package edu.stanford.cs229;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This is a base class that represents a player. All players (e.g. HumanPlayer,
 * ReinforcementLearningPlayer, etc.) should extend this class.  The game engine
 * calls these funcitons directly.
 * 
 * @author ago
 * 
 */
public abstract class AbstractPlayer implements Serializable {
	public static Logger logger = Logger
			.getLogger("edu.stanford.cs229.AbstractPlayer");

	protected final String name;

	protected Hand hand;

	private int bankroll = 10000; // initially, everyone is given $10000

	private int pot = 0; //how much the player has put into the current round

	public AbstractPlayer(String name) {
		hand = new Hand();
		this.name = name;
	}
	
	public int getId() {
		return 0;
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

	public void addPotByCalling(int pot) {
		logger.fine(name + " calls $" + pot);
		this.pot += pot;
	}

	public void addPotByBlind(int amount) {
		this.pot += amount;
	}

	public int getBankroll() {
		return bankroll - pot;
	}

	public void adjustBankroll(int i) {
		bankroll += i;
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

	public void clearCards() {
		hand = new Hand();
	}
	
	/**
	 * Called at the end of each game, to see if player wants to continue playing.
	 * This is needed when a human is playing (i.e. HumanPlayer or WebPlayer)
	 * @return
	 */
	public boolean isDonePlaying() {
		return false;
	}

	public String toString() {
		String s = "";
		for (Card c : hand.getPlayerCards()) {
			s += "[" + c.toString() + "]";
		}
		return s;
	}

	public abstract PlayerAction getAction(GameState state) throws ApplicationException;
	
	public void debugResults() {}
	
	public void processEndOfGame(int resultState) {
		pot = 0;
	}
	
}
