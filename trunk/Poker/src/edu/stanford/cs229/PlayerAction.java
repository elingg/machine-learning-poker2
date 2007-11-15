package edu.stanford.cs229;

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
