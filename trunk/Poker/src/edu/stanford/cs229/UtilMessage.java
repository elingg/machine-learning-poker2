package edu.stanford.cs229;

/**
 * This is a utility method for putting messages to HTML.
 * @author ago
 *
 */
public class UtilMessage {

	/**
	 * Returns a string (e.g. full house or two pair) that represents the hand
	 * 
	 * @param hand
	 * @return
	 */
	public static String getHandValueAsString(Hand hand) {
		int value = Util.computeValue(hand);
		switch (value) {
		case 700:
			return "Four of a Kind";
		case 600:
			return "Full House";
		case 500:
			return "Flush";
		case 400:
			return "Straight";
		case 300:
			return "Three of a Kind";
		case 200:
			return "Two Pair";
		case 100:
			return "One Pair";
		case 0:
			return "High Card";
		default:
			return "";
		}
	}
	
	
	public static String printDealerIfNecessary(GameState gameState, AbstractPlayer p) {
		if(gameState.isBigBlind(p)) {
			return "";
		} else {
			return " (Dealer)";
		}		
	}
}
