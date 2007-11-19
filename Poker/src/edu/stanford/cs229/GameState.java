package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author ago
 *
 */
public class GameState {
	Logger logger = Logger.getLogger("edu.stanford.cs229.GameState");
	
	private final List<AbstractPlayer> players;
	private final List<PlayerActvityRecord> playerActivityRecords;
	/**
	 * Constructor
	 *
	 */
	public GameState(List<AbstractPlayer> players) {
		playerActivityRecords = new ArrayList<PlayerActvityRecord>();
		this.players = players;
	}
	
	public int getTotalPot() {
		int totalPot = 0;
		for(AbstractPlayer player : players) {
			totalPot += player.getPot();
		}
		return totalPot;
	}
	
	public AbstractPlayer getOpponent() {
		for(AbstractPlayer player : players) {
			if(player.getName().equals(Constants.WEBAPP_OPPONENT_NAME)) {
				return player;
			}
		}
		return null;
	}
	
	public void addPlayerActivityRecord(PlayerActvityRecord record) {
		playerActivityRecords.add(record);
	}

	public List<PlayerActvityRecord> getPlayerActivityRecords() {
		return playerActivityRecords;
	}
	
	/**
	 * Convenience method. Indicates if it is the end of a game. This looks at
	 * the playerActivityRecords and see if the last item was a resultState.
	 * 
	 * @return
	 */
	public boolean isEndOfGame() {
		for(PlayerActvityRecord r : playerActivityRecords) {
			logger.info("Record: " + r.toString());
		}
		System.out.println("Activity Record size: " + playerActivityRecords.size());
		System.out.println("Result state is: " + playerActivityRecords.get(playerActivityRecords.size() - 1).getResultState());
		if(playerActivityRecords.get(playerActivityRecords.size() - 1).getResultState() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
