package edu.stanford.cs229;


/**
 * Represents a bet type:
 * <ul>
 * <li>small blind</li>
 * <li>big blind</li>
 * <li>bet</li>
 * <li>raise</li>
 * </ul>
 * This uses the typesafe enum pattern.
 * @author ago
 *
 */
public class BetType {
	private final String betType;
	
	//Private constructor prevents instantiation.
	private BetType(String betType) {
		this.betType = betType;
	}
	
	public final static BetType SMALL_BLIND = new BetType("small blind");
	public final static BetType BIG_BLIND = new BetType("big blind");
	public final static BetType BET = new BetType("bet");
	public final static BetType RAISE = new BetType("raise");
}
