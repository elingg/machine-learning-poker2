package edu.stanford.cs229;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * This is a base class that represents a player. All players (e.g. HumanPlayer,
 * ReinforcementLearningPlayer, etc.) should extend this class. The game engine
 * calls these funcitons directly.
 * 
 * There are several "hooks" that your player can have into the game engine. The
 * ones you will probably want to use are:
 * <ul>
 * <li>getAction - this is how the player indicates a fold, check/call, and
 * bet/raise</li>
 * <li>processEndOfGame - this is called at the end of a game. This will give
 * you an indication if your player won or loss</li>
 * <li>debugResults - this is called right before the program ends. This is
 * useful for debugging purposes</li>
 * <li></li>
 * </ul>
 * 
 * @author ago
 * 
 */
public abstract class AbstractPlayer implements Serializable {
	public static Logger logger = Logger
			.getLogger("edu.stanford.cs229.AbstractPlayer");

	private static int DEFAULT_BANKROLL = 1000; // initially, everyone is given $1000
	
	private String id;
	
	private String name;

	protected Hand hand;

	private int bankroll = DEFAULT_BANKROLL; 

	private int pot = 0; //how much the player has put into the current round

	public AbstractPlayer(String name) {
		hand = new Hand();
		this.name = name;
		this.id = name + "-" + System.currentTimeMillis();
	}
	
	public AbstractPlayer(String name, String id) {
		hand = new Hand();
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Returns the ID of the player. This is important for human players. This
	 * will either be the IP address of the user, or the Facebook ID of the
	 * player.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the name of the player.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets name of bot
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the hand of the player. 
	 * @return
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * Returns the amount of money the player has put into the pot for this round
	 * @return
	 */
	public int getPot() {
		return pot;
	}

	/**
	 * Adds money to this players pot.  This is used by the game engine only.
	 * @param amount
	 */
	void addPot(int amount) {
		this.pot += amount;
	}

	/**
	 * Returns the bankroll of the player. This takes into account how much the
	 * player has added to the pot already.
	 * 
	 * @return
	 */
	public int getBankroll() {
		return bankroll - pot;
	}
	
	/**
	 * Resets the bankroll.
	 * TODO: This should be default access, instead of public
	 */
	public void resetBankroll() {
		this.bankroll = DEFAULT_BANKROLL;
	}

	/**
	 * Adjusts the bankroll of the player.  
	 * @param i The amount to adjust the bankroll.  The amount is always added.
	 */
	void adjustBankroll(int i) {
		bankroll += i;
	}
	
	/**
	 * Clears the player cards.  This needs to be called for each game round.
	 *
	 */
	void clearCards() {
		hand = new Hand();
	}
	
	/**
	 * Prints out a player's hand.
	 */
	public String toString() {
		String s = "";
		for (Card c : hand.getPlayerCards()) {
			s += "[" + c.toString() + "]";
		}
		return s;
	}

	/**
	 * Adds a table card to the player. If you override this, make sure you call
	 * super.addTableCard() on this.
	 * 
	 * @param card
	 */
	public void addTableCard(Card card) {
		logger.finest("Table received card:" + card.getSuit() + ","
				+ card.getValue());
		hand.addTableCard(card);
	}

	/**
	 * Adds a player card. If you override this, make sure you call
	 * super.addPlayerCard()
	 * 
	 * @param card
	 */
	public void addPlayerCard(Card card) {
		logger.finest("Player received card:" + card.getSuit() + ","
				+ card.getValue());
		hand.addPlayerCard(card);
	}
	
	/**
	 * Requests action from the player
	 * @param state
	 * @return
	 * @throws ApplicationException
	 */
	public abstract PlayerAction getAction(GameState state) throws ApplicationException;
	

	/**
	 * Called after a game round ends
	 * @param resultState
	 * TODO:  Rename this to "processEndOfRound"
	 */
	public void processEndOfGame(int resultState) {
		pot = 0;
	}

	/**
	 * Called at the end of each game, to see if player wants to continue playing.
	 * This is needed when a human is playing (i.e. HumanPlayer or WebPlayer)
	 * @return
	 */
	public boolean isDonePlaying() {
		return false;
	}
	
	/**
	 * Called when the application ends.
	 */
	public void debugResults() {}
}
