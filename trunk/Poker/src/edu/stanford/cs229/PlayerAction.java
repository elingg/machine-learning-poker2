package edu.stanford.cs229;

import java.util.logging.Logger;

/**
 * Represents a player action, which consists of:
 * <ul>
 * <li>an action (bet, fold, check)</li>
 * <li>a bet (if necessary)</li>
 * </ul>
 * @author ago
 *
 */
public class PlayerAction {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.PlayerAction");
	
	private final int actionType;
	private int bet;
	private BetType betType;
	private long responseTime;
	private String message;
	
	public PlayerAction(int actionType, int bet) {
		this.actionType = actionType;
		this.bet = bet;
	}
	
	public PlayerAction(int actionType, int bet, long responseTime) {
		this.actionType = actionType;
		this.bet = bet;
		this.responseTime = responseTime;
		logger.fine("Response Time was: " + responseTime);
	}

	public int getActionType() {
		return actionType;
	}

	public int getBet() {
		return bet;
	}
	
	/**
	 * Overrides bets.  This is used by the game engine only.
	 * @param bet
	 */
	void overrideBet(int bet) {
		this.bet = bet;
	}	
	
	/**
	 * Sets the bet type (small blind, big blind, call, or raise). This is used
	 * by the game engine only.
	 * 
	 * @param betType
	 */
	void setBetType(BetType betType) {
		this.betType = betType;
	}
	
	/**
	 * Gets the bet type (small blind, big blind, call, or raise). This is used
	 * by the game engine only.
	 * @return
	 */
	BetType getBetType() {
		return betType;
	}

	/**
	 * Returns response time for the user's action
	 * @return
	 */
	public long getResponseTime() {
		return responseTime;
	}

	/**
	 * Gets a message that can be displayed to a human player.
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets a message that can be displayed to a human player.
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
