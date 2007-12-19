package edu.stanford.cs229;

import java.util.ArrayList;
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
		//System.out.println("Best Hand 1:"+Hand1+" Best Hand 2:"+Hand2);
		int hand1val = hand1.getValue();
		int hand2val = hand2.getValue();
		//System.out.println(hand1val + "  " + hand2val);
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
				else if (Hand1.get(1).getValue() > Hand2.get(1).getValue())
					return hand1;
				else if (Hand1.get(1).getValue() < Hand2.get(1).getValue())
					return hand2;
				return null;
			} else if (hand1val == 600) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				else if (Hand1.get(1).getValue() > Hand2.get(1).getValue())
					return hand1;
				else if (Hand1.get(1).getValue() < Hand2.get(1).getValue())
					return hand2;
				return null;
			} else if (hand1val == 500) {
				for (int i = 4; i >= 0; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			} else if (hand1val == 400) {
				if (Hand1.get(1).getValue() > Hand2.get(1).getValue()) // 1 used just to make sure A2345 straight doesnt mess up
					return hand1;
				else if (Hand1.get(1).getValue() < Hand2.get(1).getValue())
					return hand2;
				return null;
			} else if (hand1val == 300) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				for (int i = Hand1.size()-1; i >= 1; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			}
			else if (hand1val == 200) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				else if (Hand1.get(1).getValue() > Hand2.get(1).getValue())
					return hand1;
				else if (Hand1.get(1).getValue() < Hand2.get(1).getValue())
					return hand2;
				else if(Hand1.size()>=3){
					if (Hand1.get(2).getValue() > Hand2.get(2).getValue())
						return hand1;
					else if (Hand1.get(2).getValue() < Hand2.get(2).getValue())
						return hand2;
				}
				return null;
			} else if (hand1val == 100) {
				if (Hand1.get(0).getValue() > Hand2.get(0).getValue())
					return hand1;
				else if (Hand1.get(0).getValue() < Hand2.get(0).getValue())
					return hand2;
				for (int i = Hand1.size()-1; i >= 1; i--) {
					if (Hand1.get(i).getValue() > Hand2.get(i).getValue())
						return hand1;
					else if (Hand1.get(i).getValue() < Hand2.get(i).getValue())
						return hand2;
				}
				return null;
			} else {
				for (int i = Hand1.size()-1; i>= 0; i--) {
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
	
	public static float computeWinProbability (Hand hand){
		
		List<Card> allCards = new ArrayList<Card>();
		List<Card> tableCards = hand.getTableCards();
		List<Card> playerCards = hand.getPlayerCards();
		int totalHands = 0;
		int betterHands = 0;
		Card temp;
		//System.out.println(twoCardHands.size());
		if(allCards.size()==0){
			for(int i = 2; i<=14; i++){
				for(int j = 1; j<=4; j++){
					Card c = new Card(i,j);
					allCards.add(c);
				}
			}
		}
		int isDuplicate =0;
		for(int i=0; i<allCards.size()-1; i++){
			for(int j=i+1; j<allCards.size(); j++){
						Card c1 = allCards.get(i);
						Card c2 = allCards.get(j);
						isDuplicate = 0;
						//twoCardHands.add(temp);
						for(int k = 0; k< tableCards.size(); k++)
							if( (c1.getSuit()== tableCards.get(k).getSuit() && c1.getValue()== tableCards.get(k).getValue()) || (c2.getSuit()== tableCards.get(k).getSuit() && c2.getValue()== tableCards.get(k).getValue()) ){
								isDuplicate =1;
								break;
							}
						for(int k = 0; k< playerCards.size(); k++)
							if( (c1.getSuit()== playerCards.get(k).getSuit() && c1.getValue()== playerCards.get(k).getValue()) || (c2.getSuit()== playerCards.get(k).getSuit() && c2.getValue()== playerCards.get(k).getValue()) ){
								isDuplicate = 1;
								break;
							}
						if(isDuplicate==1)
							continue;
						Hand oppHand = new Hand();
						oppHand.addPlayerCard(c1);
						oppHand.addPlayerCard(c2);
						for(int m=0; m<tableCards.size(); m++)
							oppHand.addTableCard(tableCards.get(m));
						Hand winningHand = Util.findWinner(hand, oppHand);
						if(winningHand == oppHand)
							betterHands++;
						totalHands++;
			}
		}
		//System.out.println(totalHands);
		return ( 1 - ( (float) betterHands/ (float) totalHands ) );
	}

	public static float computeOuts(Hand hand)
	{
		Deck tempDeck1= new Deck();
		Deck tempDeck2= new Deck();
		Deck tempDeck3 = new Deck();
		Deck tempDeck4 = new Deck();
		Card card1;
		Card card2;
		Card card3;
		Card card4;
		
		int numLosses=0;
		int numRounds=0;
		List<Card> tableCards = hand.getTableCards();
		List<Card> myCards = hand.getPlayerCards();
		
		Hand myHand= new Hand();
		Hand opponentsHand= new Hand();
		
		for(int i=1; i<=52; i++)
		{
			
			card1 = tempDeck1.drawCard();
			if (tableCards.contains(card1) || myCards.contains(card1))
				continue;
			tempDeck2.shuffleDeck();
			for(int j=1; j<=52; j++)
			{
			
				card2=tempDeck2.drawCard();
				if(tableCards.contains(card2) || myCards.contains(card2) || card1.equals(card2))
					continue;
			
				/*tempDeck3.shuffleDeck();
				if(hand.getTableCards().size() ==3)
				{

					for(int k=1; k<=52; k++)
					{
						
						card3=tempDeck3.drawCard();
						if(tableCards.contains(card3) || myCards.contains(card3) || card1.equals(card3)  || card2.equals(card3) )
							continue;
					
						tempDeck4.shuffleDeck();
						for(int l=1; l<=52; l++)
						{
							
							card4=tempDeck4.drawCard();
							if(tableCards.contains(card4) || myCards.contains(card4) || card1.equals(card4)  || card2.equals(card4)  || card3.equals(card4))
								continue;
							
							myHand= new Hand();
							opponentsHand= new Hand();
					
							ArrayList<Card> test1= new ArrayList<Card>();
							test1.add(card3);
							test1.add(card4);
							test1.addAll(hand.getTableCards());
							test1.addAll(hand.getPlayerCards());
							myHand.setAllCards(test1);
							
		
							ArrayList<Card> test= new ArrayList<Card>();
							test.add(card1);
							test.add(card2);
							test.add(card3);
							test.add(card4);
							test.addAll(hand.getTableCards());
							opponentsHand.setAllCards(test);
						
							Hand winningHand=Util.findWinner(myHand, opponentsHand);
							if(winningHand!=null && winningHand.equals(opponentsHand))
									numLosses++;
							numRounds++;
							
						}
						
					}
				}
				
				else if (hand.getTableCards().size()==4)
				{
					
					for(int k=1; k<=52; k++)
					{
						card3=tempDeck3.drawCard();
						if(tableCards.contains(card3) || myCards.contains(card3) || card1.equals(card3)  || card2.equals(card3) )
							continue;
						
						myHand= new Hand();
						opponentsHand= new Hand();
				
						ArrayList<Card> test1= new ArrayList<Card>();
						test1.add(card3);
						test1.addAll(hand.getTableCards());
						test1.addAll(hand.getPlayerCards());
						myHand.setAllCards(test1);
						
					
						ArrayList<Card> test= new ArrayList<Card>();
						test.add(card1);
						test.add(card2);
						test.add(card3);
						test.addAll(hand.getTableCards());
						opponentsHand.setAllCards(test);
					
						
						Hand winningHand=Util.findWinner(myHand, opponentsHand);
						if(winningHand!=null && winningHand.equals(opponentsHand))
								numLosses++;
						numRounds++;
						
						
					}
				}*/
				//else
				{
					myHand= new Hand();
					opponentsHand= new Hand();
			
					myHand.setAllCards(hand.getAllCards());
					
					ArrayList<Card> test= new ArrayList<Card>();
					test.add(card1);
					test.add(card2);
					test.addAll(hand.getTableCards());
					opponentsHand.setAllCards(test);
					
					
					Hand winningHand=Util.findWinner(myHand, opponentsHand);
					if(winningHand!=null && winningHand.equals(opponentsHand))
							numLosses++;
					numRounds++;
					
					
				}
				
			}
		}
		return (float)numLosses/(float)numRounds;
	}
}
