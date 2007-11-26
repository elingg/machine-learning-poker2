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
	private final List<PlayerActivityRecord> playerActivityRecords;
	
	
	/**
	 * Constructor
	 *
	 */
	public GameState(List<AbstractPlayer> players) {
		playerActivityRecords = new ArrayList<PlayerActivityRecord>();
		this.players = players;
	}
	
	/**
	 * Convenience method to get the total pot in the game
	 * @return
	 */
	public int getTotalPot() {
		logger.finest("Entering getTotalPot");
		int totalPot = 0;
		for(AbstractPlayer player : players) {
			logger.finest(player.getName() + ":" + player.getPot());
			totalPot += player.getPot();
		}
		logger.finest("Pot: " + totalPot);
		return totalPot;
	}
	
	/**
	 * Gets the opponent in a web application
	 * @return
	 */
	/*
	public AbstractPlayer getOpponent() {
		for(AbstractPlayer player : players) {
			if(player.getName().equals(Constants.WEBAPP_OPPONENT_NAME)) {
				return player;
			}
		}
		throw new IllegalArgumentException("List of players is malformed");
	}
	*/
	
	/**
	 * Returns the opponent. From this, you can rerieve information about your
	 * opponent.
	 * 
	 * @param me
	 * @return
	 */
	public AbstractPlayer getOpponent(AbstractPlayer me) {
		for(AbstractPlayer player : players) {
			if(me.getName() != player.getName()) {
				return player;
			}
		}
		throw new IllegalArgumentException("List of players is malformed");
	}
	
	/**
	 * Adds a player activity record.  This is used by the game engine only.
	 * @param record
	 */
	void addPlayerActivityRecord(PlayerActivityRecord record) {
		playerActivityRecords.add(record);
	}

	/**
	 * Returns the player activity record for this round of the game.
	 * @return
	 */
	public List<PlayerActivityRecord> getPlayerActivityRecords() {
		return playerActivityRecords;
	}
	
	/**
	 * Convenience method. Indicates if it is the end of a game. This looks at
	 * the playerActivityRecords and see if the last item was a resultState.
	 * 
	 * @return
	 */
	public boolean isEndOfGame() {
		for(PlayerActivityRecord r : playerActivityRecords) {
			logger.fine("Record: " + r.toString());
			//If there is a record that has a result state, return true
			if(r.getResultState() > 0)
				return true;
		}
		return false;
	}
}
