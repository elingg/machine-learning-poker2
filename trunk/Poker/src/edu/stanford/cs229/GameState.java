package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author alecmgo@gmail.com
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
		this.players = players;
		playerActivityRecords = new ArrayList<PlayerActivityRecord>();
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
	 * Return's opponent's bet, as a convenient method. If the opponent folds,
	 * checks, or calls, this returns 0. Otherwise, it will return the amount
	 * that the player bet.
	 * 
	 * @param me
	 * @return
	 */
	public int getOpponentBet(AbstractPlayer me) {
		AbstractPlayer opponent = getOpponent(me);
		
		PlayerActivityRecord a = playerActivityRecords.get(playerActivityRecords.size() - 1);
		PlayerAction playerAction = a.getPlayerAction();
		
		if(opponent.getName().equals(a.getName()) && (playerAction.getActionType() == ActionType.BET_OR_RAISE)) {
			return playerAction.getBet();
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns true if the player passed in is the big blind.
	 * @param me
	 * @return
	 */
	public boolean isBigBlind(AbstractPlayer me) {
		return playerActivityRecords.get(1).getName().equals(me.getName());
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
	 * Returns true if players must show cards.  This happens if nobody has folded,
	 * and it is the end of the game.  
	 * 
	 * @return
	 */
	public boolean isShowCards() {
		for(PlayerActivityRecord r : playerActivityRecords) {
			//If somebody has folded, then there is no need to show cards
			if((r.getPlayerAction() != null) && (r.getPlayerAction().getActionType() == ActionType.FOLD))
				return false;
		}
		return isEndOfGame();
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
	
	public int getMinimumRaiseAmount() {
		int minimumRaiseAmount = 0;
		for(PlayerActivityRecord r : playerActivityRecords) {
			if(r.getPlayerAction().getActionType() == ActionType.BET_OR_RAISE) {
				minimumRaiseAmount = r.getPlayerAction().getBet();
			}
		}
		return minimumRaiseAmount;
	}
	
	/**
	 * Compares the pots of Player 1 and Player 2
	 * @return true if the pots are equal, otherwise false
	 */
	public boolean arePotsEqual() {
		if(players.get(0).getPot() != players.get(1).getPot()) {
			return false;
		} else {
			return true;
		}		
	}
	
}
