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
	/**
	 * Constructor
	 *
	 */
	public GameState(List<AbstractPlayer> players) {
		this.players = players;
	}
	
	public int getTotalPot() {
		int totalPot = 0;
		for(AbstractPlayer player : players) {
			totalPot += player.getPot();
		}
		return totalPot;
	}
}
