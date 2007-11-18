package edu.stanford.cs229;

/**
 * Represents a random player
 * @author ago
 * TODO: Rename to RandomPlayer
 */
public class RandomPlayer extends AbstractPlayer{
	public RandomPlayer(String name) {
		super(name);
	}
	
	/**
	 * Player action for dealer.
	 */
	public PlayerAction getAction(GameState state) {
		double decision = Math.random();
		
		if(decision < 0.33) {
			return new PlayerAction(ActionType.CHECK_OR_CALL, 0);
		} else if(decision < 0.66) {
			return new PlayerAction(ActionType.FOLD, 0);
		} else {
			//Always bet 10
			return new PlayerAction(ActionType.BET_OR_RAISE, 10);
		}
	}
}
