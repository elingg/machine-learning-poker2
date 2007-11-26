package edu.stanford.cs229;

/**
 * Represents a random player
 * @author ago
 */
public class RandomPlayer extends AbstractPlayer{
	private double foldPercent = 0.05;
	private double checkOrCallPercent = 0.20;
	private double betOrRaisePercent = 0.75;
	
	private int maxRuns = 0;
	private int numRuns = 0;
	
	public RandomPlayer(String name) {
		super(name);
	}
	
	/**
	 * Initializes the random player with probabilities for folding,
	 * checking/calling, and betting/raising. These values must add up to 1.0.
	 * 
	 * @param name
	 * @param foldPercent -
	 *            probability of folding
	 * @param checkOrCallPercent -
	 *            probability of checking/calling
	 * @param betOrRaisePercent -
	 *            probability of betting/raising
	 * @param maxRuns -
	 *            number of times the random player should run. Zero (0)
	 *            indicates that the bot should run indefinitely times.
	 */
	public RandomPlayer(String name, float foldPercent, float checkOrCallPercent, float betOrRaisePercent, int maxRuns) {
		super(name);
		if(foldPercent + checkOrCallPercent + betOrRaisePercent != 1.0) {
			throw new IllegalArgumentException("Percentages do not add up to 1");
		}
		this.foldPercent = foldPercent;
		this.checkOrCallPercent = checkOrCallPercent;
		this.betOrRaisePercent = betOrRaisePercent;
		this.maxRuns = maxRuns;
	}

	
	/**
	 * Player action for dealer.
	 */
	public PlayerAction getAction(GameState state) {
		double decision = Math.random();
		
		if(decision < foldPercent) {
			return new PlayerAction(ActionType.FOLD, 0);
		} else if(decision < foldPercent + checkOrCallPercent) {
			return new PlayerAction(ActionType.CHECK_OR_CALL, 0);
		} else {
			//Always bet 10
			return new PlayerAction(ActionType.BET_OR_RAISE, 10);
		}
	}
	
	public boolean isDonePlaying() {
		//If max runs is set, do continue indefinitely.
		if(maxRuns == 0) {
			return false;
		}
		
		//Honor max runs if it is set.
		numRuns++;
		if(numRuns < maxRuns) {
			return false;
		} else {
			return true;
		}
	}
	
	
}
