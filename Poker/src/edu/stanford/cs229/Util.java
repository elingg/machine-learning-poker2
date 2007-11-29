package edu.stanford.cs229;
import java.util.Collections;
import java.util.List;

public class Util {
	static final int straightflush=800;
	static final int fourkind=700;
	static final int fullhouse=600;
	static final int flush=500;
	static final int straight=400;
	static final int threekind=300;
	static final int twopair=200;
	static final int pair=100;
	static final int highcard = 0;
	
	/**
	 * Private constructor ensures that this is never instantiated
	 *
	 */
	private Util() {}

	public static int findHighCardValue(List<Card> cardList)
	{		
		//TODO: Elizabeth, is it okay if I add a size check here?
		if(cardList!=null && cardList.size() > 0)
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
	//	System.out.println(hand+" ");
		if (hand.isStraightFlush() != null) {
			hand.setValue(straightflush);
			return hand.isStraightFlush();
		} else if (hand.isFourKind() != null) {
			hand.setValue(fourkind);
			return hand.isFourKind();
		} else if (hand.isFullHouse() != null) {
			hand.setValue(fullhouse);
			return hand.isFullHouse();
		}else if (hand.isFlush() != null) {
				hand.setValue(flush);
				return hand.isFlush();	
		} else if (hand.isStraight() != null) {
			hand.setValue(straight);
			return hand.isStraight();
		}  else if (hand.isThreeKind() != null) {
			hand.setValue(threekind);
			return hand.isThreeKind();
		} else if (hand.isTwoPair() != null) {
			hand.setValue(twopair);
			return hand.isTwoPair();
		} else if (hand.isPair() != null) {
			hand.setValue(pair);
			return hand.isPair();
		} else {
			hand.setValue(highcard);
			return hand.highCard();
		}
		
	}
	
	public static Hand findWinner(Hand hand1, Hand hand2)
	{	
		List<Card> Hand1 = findBestCards(hand1);
		List<Card> Hand2 = findBestCards(hand2);

		int hand1val = hand1.getValue();
		int hand2val = hand2.getValue();
		System.out.println(hand1val + "  " + hand2val);
		if (hand1val > hand2val)
			return hand1;
		else if (hand2val > hand1val)
			return hand2;
		else { 
			// there is a tie, which means both hands have the same combination, it
			// comes down to the kicker
			if (hand1val == 800) {
				for (int i = 4; i <= 0; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			} else if (hand1val == 700) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				return null; // Two quads cannot be equal
			} else if (hand1val == 600) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				return null; // Two full-houses cannot be equal
			} else if (hand1val == 500) {
				for (int i = 4; i <= 0; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			} else if (hand1val == 400) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				return null;
			} else if (hand1val == 300) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				return null; // two three of a kinds cannot be equal
			} else if (hand1val == 200) {
				if (Hand1.get(1).getValue() > Hand2.get(1).getValue())
					return hand1;
				else if (Hand1.get(1).getValue() < Hand2.get(1).getValue())
					return hand2;
				else if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				else if (Hand1.get(2).getValue() > Hand2.get(2).getValue())
					return hand1;
				else if (Hand1.get(2).getValue() < Hand2.get(2).getValue())
					return hand2;
				return null;
			} else if (hand1val == 100) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				for (int i = 3; i <= 0; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			} else {
				for (int i = 4; i>= 0; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			}
		
		}
	}
	
	public static int computeValue(Hand hand)
	{
		List<Card> bestHand = findBestCards(hand);
		int handval = hand.getValue();
		return handval;
	}
}
