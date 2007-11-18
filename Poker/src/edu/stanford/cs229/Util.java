package edu.stanford.cs229;
import java.util.Collections;
import java.util.List;

public class Util {
	static final int straightflush=800;
	static final int fourkind=700;
	static final int fullhouse=600;
	static final int straight=500;
	static final int flush=400;
	static final int threekind=300;
	static final int twopair=200;
	static final int pair=100;
	
	/**
	 * Private constructor ensures that this is never instantiated
	 *
	 */
	private Util() {}

	public static int findHighCardValue(List<Card> cardList)
	{		
		if(cardList!=null)
		{
			Collections.sort(cardList);
			return cardList.get(cardList.size()-1).getValue();
		}
		return -1;
	}
	
	/**
	 * Finds the best cards.
	 * @param hand
	 * @return
	 */
	public static List<Card> findBestCards(Hand hand) {
		if (hand.isStraightFlush() != null) {
			hand.setValue(straightflush);
			return hand.isStraightFlush();
		} else if (hand.isFourKind() != null) {
			hand.setValue(fourkind);
			return hand.isFourKind();
		} else if (hand.isFullHouse() != null) {
			hand.setValue(fullhouse);
			return hand.isFullHouse();
		} else if (hand.isStraight() != null) {
			hand.setValue(straight);
			return hand.isStraight();
		} else if (hand.isFlush() != null) {
			hand.setValue(flush);
			return hand.isFlush();
		} else if (hand.isThreeKind() != null) {
			hand.setValue(threekind);
			return hand.isThreeKind();
		} else if (hand.isTwoPair() != null) {
			hand.setValue(twopair);
			return hand.isTwoPair();
		} else if (hand.isPair() != null) {
			hand.setValue(pair);
			return hand.isPair();
		}
		return null;
	}
	
	public static Hand findWinner(Hand hand1, Hand hand2)
	{	
		int hand1val=computeValue(hand1);
		int hand2val=computeValue(hand2);
		
		if(hand1val > hand2val)
			return hand1;
		else if(hand2val > hand1val)
			return hand2;
		else
			return null;		
	}
	
	public static int computeValue(Hand hand)
	{
		findBestCards(hand);
		int handval;
		
		if (hand.getValue() == -1) {
			handval = findHighCardValue(hand.getAllCards());
		} else
			handval=hand.getValue() + findHighCardValue(findBestCards(hand));
		return handval;
	}
}
