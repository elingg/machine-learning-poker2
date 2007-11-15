package edu.stanford.cs229;

/**
 * Represents a player action, which consists of:
 * - an action (bet, fold, check)
 * - a bet (if necessary)
 * @author ago
 *
 */
public class PlayerAction {
	private final int actionType;
	private final int bet;
	
	public PlayerAction(int actionType, int bet) {
		this.actionType = actionType;
		this.bet = bet;
	}

	public int getActionType() {
		return actionType;
	}

	public int getBet() {
		return bet;
	}
}
