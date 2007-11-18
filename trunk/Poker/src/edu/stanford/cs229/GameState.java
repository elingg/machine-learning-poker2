package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author ago
 *
 */
public class GameState {

	private final List<AbstractPlayer> players;
	private final List<PlayerActvityRecord> playerActionRecords;
	/**
	 * Constructor
	 *
	 */
	public GameState(List<AbstractPlayer> players) {
		playerActionRecords = new ArrayList<PlayerActvityRecord>();
		this.players = players;
	}
	
	public int getTotalPot() {
		int totalPot = 0;
		for(AbstractPlayer player : players) {
			totalPot += player.getPot();
		}
		return totalPot;
	}
	
	public AbstractPlayer getOpponent(String name) {
		for(AbstractPlayer player : players) {
			if(player.getName().equals(Constants.WEBAPP_OPPONENT_NAME)) {
				return player;
			}
		}
		return null;
	}
	
	public void addPlayerActionRecord(PlayerActvityRecord record) {
		playerActionRecords.add(record);
	}

	public List<PlayerActvityRecord> getPlayerActionRecords() {
		return playerActionRecords;
	}
}
