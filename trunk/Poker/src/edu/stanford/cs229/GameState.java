package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author ago
 *
 */
public class GameState {

	private int player1Pot = 0;
	private int player2Pot = 0;
	/**
	 * Constructor
	 *
	 */
	public GameState() {
		
	}
	
	public void processPlayer1Action(AbstractPlayer player, PlayerAction action) {
		if(action.getActionType() == ActionType.BET) {
			System.out.println(player.getName() + " bets " + action.getBet());
			player1Pot += action.getBet();
		}
	}
	
	public void processPlayer2Action(AbstractPlayer player, PlayerAction action) {
		if(action.getActionType() == ActionType.BET) {
			System.out.println(player.getName() + " bets " + action.getBet());
			player1Pot += action.getBet();
		}
	}
	
	public int getTotalPot() {
		return player1Pot + player2Pot;
	}

	public int getPlayer1Pot() {
		return player1Pot;
	}

}
