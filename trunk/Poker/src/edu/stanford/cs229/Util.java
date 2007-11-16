package edu.stanford.cs229;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {
	
	public Util()
	{
		
	}
	
	int straightflush=800;
	int flush=700;
	int straight=600;
	int fourkind=500;
	int fullhouse=400;
	int threekind=300;
	int twopair=200;
	int pair=100;
	

	public int findHighCardValue(List<Card> cardList)
	{
		
		
		if(cardList!=null)
		{
			Collections.sort(cardList);
			return cardList.get(cardList.size()-1).getValue();
		}
		
		return -1;
	}
	
	public List<Card> findBestCards(Hand hand)
	{
		if(hand.isStraightFlush()!=null)
		{
			hand.setValue(straightflush);
			return hand.isStraightFlush();
		}
		else if(hand.isFlush()!=null)
		{
			hand.setValue(flush);
			return hand.isFlush();
		}
		else if(hand.isStraight()!=null)
		{
			hand.setValue(straight);
			return hand.isStraight();
		}
		else if(hand.isFourKind()!=null)
		{
			hand.setValue(fourkind);
			return hand.isFourKind();
		}
		else if(hand.isFullHouse()!=null)
		{
			hand.setValue(fullhouse);
			return hand.isFullHouse();
		}
		else if(hand.isThreeKind()!=null)
		{
			hand.setValue(threekind);
			return hand.isThreeKind();
		}
		else if(hand.isTwoPair()!=null)
		{
			hand.setValue(twopair);
			return hand.isTwoPair();
		}
		else if(hand.isPair()!=null)
		{
			hand.setValue(pair);
			return hand.isPair();
		}
		return null;
	}
	
	public Hand findWinner(Hand hand1, Hand hand2)
	{

		findBestCards(hand1);
		findBestCards(hand2);
		int hand1val;
		int hand2val;
		
		if(hand1.getValue()==-1 && hand2.getValue()==-1)
		{
			hand1val=findHighCardValue(hand1.getAllCards());
			hand2val=findHighCardValue(hand2.getAllCards());
		}
		else
		{
			hand1val= hand1.getValue() + findHighCardValue(findBestCards(hand1));
			hand2val= hand2.getValue() + findHighCardValue(findBestCards(hand2));
		}
		if(hand1val > hand2val)
			return hand1;
		else if(hand2val > hand1val)
			return hand2;
		else
			return null;
		
	}
  
}
