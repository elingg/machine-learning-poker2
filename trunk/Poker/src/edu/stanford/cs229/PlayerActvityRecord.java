package edu.stanford.cs229;

/***
 * Represents an "activity record" for a player
 * @author ago
 *
 */
public class PlayerActvityRecord {
	private final int playerId;
	private final String name;
	private final int gameNum;
	private final int phaseNum;
	private PlayerAction playerAction;
	private int resultState;
	
	public PlayerActvityRecord (int playerId, String name, int gameNum, int phaseNum, PlayerAction playerAction) {
		this.playerId = playerId;
		this.name = name;
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.playerAction = playerAction;
	}
	
	public PlayerActvityRecord (int playerId, String name, int gameNum, int phaseNum, int resultState) {
		this.playerId = playerId;
		this.name = name;
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.resultState = resultState;
	}
	
	public String toString() {
		if (playerAction != null) {
			if (playerAction.getActionType() == ActionType.FOLD)
				return name + " folds";
			if (playerAction.getActionType() == ActionType.BET_OR_RAISE)
				return name + " bets/raises";
			if (playerAction.getActionType() == ActionType.CHECK_OR_CALL)
				return name + " checks/calls";
		} else {
			if (resultState == ResultState.WIN) {
				return name + " wins!<br/>";
			}
			if (resultState == ResultState.TIE) {
				return name + " ties<br/>";
			}
			if (resultState == ResultState.LOSE) {
				return name + " loses<br/>";
			}
		}
		return "ERROR";
	}

	public int getGameNum() {
		return gameNum;
	}

	public String getName() {
		return name;
	}

	public int getPhaseNum() {
		return phaseNum;
	}

	public PlayerAction getPlayerAction() {
		return playerAction;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getResultState() {
		return resultState;
	}
}
