package edu.stanford.cs229;

public class PlayerActionRecord {
	private final String playerId;
	private final String name;
	private final int gameNum;
	private final int phaseNum;
	private final PlayerAction playerAction;
	
	public PlayerActionRecord (String playerId, String name, int gameNum, int phaseNum, PlayerAction playerAction) {
		this.playerId = playerId;
		this.name = name;
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.playerAction = playerAction;
	}
	
	public String toString() {
		if(playerAction.getActionType() == ActionType.FOLD)
			return name + " folds";
		if(playerAction.getActionType() == ActionType.BET_RAISE)
			return name + " bets/raises";
		if(playerAction.getActionType() == ActionType.CHECK_CALL)
			return name + " checks/calls";
		return "ERROR";
	}
}
