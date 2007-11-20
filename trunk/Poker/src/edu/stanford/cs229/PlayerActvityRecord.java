package edu.stanford.cs229;

import java.util.logging.Logger;

/***
 * Represents an "activity record" for a player
 * @author ago
 *
 */
public class PlayerActvityRecord {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.PlayerActivityRecord");
	private final int playerId;
	private final String name;
	private final int gameNum;
	private final int phaseNum;
	private Hand hand;
	private int responseTime;
	private PlayerAction playerAction;
	private int resultState;
	
	public PlayerActvityRecord (int playerId, String name, int gameNum, int phaseNum, PlayerAction playerAction) {
		this.playerId = playerId;
		this.name = name;
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.playerAction = playerAction;
		logger.fine(this.toString());
	}
	
	public PlayerActvityRecord (int playerId, String name, int gameNum, int phaseNum, int resultState) {
		this.playerId = playerId;
		this.name = name;
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.resultState = resultState;
		logger.fine(this.toString());
	}
	
	/**
	 * Prints a string version of the activity record. This is used in the HTML
	 * version
	 */
	public String toString() {
		if (playerAction != null) {
			String amountString = "";
			int amount = playerAction.getBet();
			if(amount > 0) {
				amountString += "$" + Integer.toString(amount);
			}
			if (playerAction.getActionType() == ActionType.FOLD)
				return name + " folds";
			if (playerAction.getActionType() == ActionType.BET_OR_RAISE)
				return name + " bets/raises " + amountString;
			if (playerAction.getActionType() == ActionType.CHECK_OR_CALL)
				if(amount > 0) {
					return name + " calls " + amountString;
				} else {
					return name + " checks";
				}
		} else {
			if (resultState == ResultState.WIN) {
				return "<b>" + name + " wins!</b>";
			}
			if (resultState == ResultState.TIE) {
				return "<b>" + name + " ties</b>";
			}
			if (resultState == ResultState.LOSE) {
				return "<b>" + name + " loses</b>";
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

	public Hand getHand() {
		return hand;
	}

	public int getResponseTime() {
		return responseTime;
	}
}
