package edu.stanford.cs229;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hand that may be held by a player. It also provides functions
 * for checking if a hand is a certain type (i.e. two pair, three pair).
 * 
 * The hand contains multiple lists to help keep track of player cards vs. table
 * cards.
 * 
 */
public class Hand implements Serializable {
	private List<Card> playerCards;

	private List<Card> tableCards;

	private List<Card> allCards;

	private int value = -1;

	public Hand() {
		playerCards = new ArrayList<Card>();
		tableCards = new ArrayList<Card>();
		allCards = new ArrayList<Card>();
	}
	
	public String toString() {
		String s = "T:";
		for(Card c : tableCards) {
			s += "[" + c.toString() + "]";
		}
		s += " P:";
		for(Card c : playerCards) {
			s += "[" + c.toString() + "]";
		}
		return s;
	}

	public List<Card> getTableCards() {
		return tableCards;
	}

	public void setTableCards(List<Card> tableCards) {
		this.tableCards = tableCards;
	}
	
	/**
	 * Shows the table cards as html
	 * @return
	 */
	public String getTableCardsAsHtml() {
		String s = "";
		for (Card card : tableCards) {
			s += card.toHtmlString();
		}
		for (int i = 0; i < (5 - tableCards.size()); i++) {
			s += Card.getTableCardPlaceHolderAsHtml();
		}
		return s;
	}	
	

	public List<Card> getPlayerCards() {
		return playerCards;
	}

	public void setPlayerCards(List<Card> playerCards) {
		this.playerCards = playerCards;
	}
	
	/**
	 * Shows the table cards as html
	 * @return
	 */
	public String getPlayerCardsAsHtml() {
		String s = "";
		for (Card card : playerCards) {
			s += card.toHtmlString();
		}
		return s;
	}	

	public List<Card> getAllCards() {
		return allCards;
	}

	public void setAllCards(List<Card> allCards) {
		this.allCards = allCards;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int i) {
		this.value = i;
	}

	public void addTableCard(Card card) {
		tableCards.add(card);
		allCards.add(card);
	}

	public void addPlayerCard(Card card) {
		playerCards.add(card);
		allCards.add(card);
	}

	/**
	 * Returns a list of cards in sorted order
	 * 
	 * @return
	 */
	public List<Card> getSorted() {
		Collections.sort(allCards);
		return allCards;
	}
	
	/**
	 * Gets all cards of a particular suit
	 * @param i
	 * @return
	 */
	public List<Card> getSuit(int i) {
		List<Card> newList = new ArrayList<Card>();
		for(Card card : allCards) {
			if(card.getSuit() == i) {
				newList.add(card);
			}
		}
		return newList;
	}

	public List<Card> isStraightFlush() {
		// sort all cards
		List<Card> suitList= new ArrayList<Card>();
		List<Card> tempList= new ArrayList<Card>();
		for(int n=1; n<=4; n++)
		{			
			suitList = getSuit(n);
			if(suitList.size()>=5)
			{
				// there is a flush - there can only be one flush
				// to see if there is a straight here, there cannot be duplicates
				for(int i=suitList.size()-1; i>=4; i--)
				{
					if(((suitList.get(i)).getValue() - 1 == (suitList.get(i-1)).getValue()) && ((suitList.get(i-1)).getValue() - 1 == (suitList.get(i-2)).getValue()) && ((suitList.get(i-2)).getValue() - 1 == (suitList.get(i-3)).getValue()) && ((suitList.get(i-3)).getValue() - 1 == (suitList.get(i-4)).getValue()))
					{
						tempList.add(suitList.get(i-4));
						tempList.add(suitList.get(i-3));
						tempList.add(suitList.get(i-2));
						tempList.add(suitList.get(i-1));
						tempList.add(suitList.get(i));
						//returns the top straight flush
						return tempList;
					}
				}
				return null;
			}
		}
		return null;
	}
	
	
	public List<Card> isFlush(){
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		List<Card> suitList= new ArrayList<Card>();
		for(int i=1; i<=4; i++)
		{
			// There can be only one flush
			suitList= getSuit(i);
			if(suitList.size()>=5)
			{
				suitList= suitList.subList(suitList.size()-5, suitList.size());
				// returns the top flush
				return suitList;
			}
		}
		return null;
	}
	
	public List<Card> isStraight(){
		
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		List<Card> tempCards= new ArrayList<Card>();
		for(int i=0; i<allCards.size(); i++)	// do a deep copy to clone allCards.		
			tempCards.add(allCards.get(i));
		// Removed duplicates in the list before checking for a straight
		for(int i=0; i<tempCards.size()-1; i++){
			if(tempCards.get(i).getValue() == tempCards.get(i+1).getValue())
				tempCards.remove(i);
		}
		
		for(int i=tempCards.size()-1; i>=4; i--)
		{
			if(((tempCards.get(i)).getValue() - 1 == (tempCards.get(i-1)).getValue()) && ((tempCards.get(i-1)).getValue() - 1 == (tempCards.get(i-2)).getValue()) && ((tempCards.get(i-2)).getValue() - 1 == (tempCards.get(i-3)).getValue()) && ((tempCards.get(i-3)).getValue() - 1 == (tempCards.get(i-4)).getValue()))
			{
				tempList.add(tempCards.get(i-4));
				tempList.add(tempCards.get(i-3));
				tempList.add(tempCards.get(i-2));
				tempList.add(tempCards.get(i-1));
				tempList.add(tempCards.get(i));
				// Eg. (4,5,6,7,8) returned
				return tempList;
			}
				
		}
		
		return null;
	}
	
	
	public List<Card> isFullHouse(){
		
		List<Card> tempList= new ArrayList<Card>();
		List<Card> tempList1= new ArrayList<Card>();
		List<Card> tempList2= new ArrayList<Card>();
		tempList1 = this.isThreeKind();
		if (tempList1 == null)
			return null;
		tempList2= this.isPair();

		
		if(tempList1 != null && tempList2 !=null && tempList2.get(0)!=tempList1.get(0))
		{
			
			tempList1.remove(2);
			tempList1.remove(1);
			tempList1.add(tempList2.get(0));
			// returns the trip card followed by the pair card. Eg. (3,4,4,4,7,K,K) -> [4,K]
			return tempList1;
		}
		return null;
	}
	
	public List<Card> isFourKind(){
	
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		for(int i=allCards.size()-1; i>=3; i--)
		{
			if(allCards.get(i).getValue() ==allCards.get(i-1).getValue() && allCards.get(i-1).getValue() ==allCards.get(i-2).getValue()  && allCards.get(i-2).getValue() ==allCards.get(i-3).getValue() )
			{	
				tempList.add(allCards.get(i-3));	
				if(i==allCards.size()-1) // if the top card is the quad card
					if(i>=4)
						tempList.add(allCards.get(i-4));	// Kicker card
				else tempList.add(allCards.get(allCards.size()-1));
				// 2 cards are returned, 1st is quad card, 2nd is the kicker (kicker doesn't really matter here)
				return tempList;
			}
				
		}
		
		return null;
	}
	
	public List<Card> isThreeKind(){
		
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		List<Card> tempCards= new ArrayList<Card>();
		for(int i=0; i<allCards.size(); i++)	// do a deep copy to clone allCards.		
			tempCards.add(allCards.get(i));
		for(int i=tempCards.size()-1; i>=2; i--)
		{
			if(tempCards.get(i).getValue() ==tempCards.get(i-1).getValue() && tempCards.get(i-1).getValue() ==tempCards.get(i-2).getValue() )
			{
				tempList.add(tempCards.get(i-2));
				tempCards.remove(i);
				tempCards.remove(i-1);
				tempCards.remove(i-2);
				if(tempCards.size()>=2){
					tempList.add(tempCards.get(tempCards.size()-2));
					tempList.add(tempCards.get(tempCards.size()-1));
				}
				// returns 3 cards - 1st the trip card and then the top two kickers (kickers don't really matter here). Eg. if the hand is (4,6,6,6,J,K,A), [6,K,A] is returned
				return tempList;
			}
		}
		return null;
	}
	
	public List<Card> isTwoPair()
	{
		this.getSorted();
		int numPairs=0;
		List<Card> tempList= new ArrayList<Card>();
		List<Card> tempCards= new ArrayList<Card>();
		List<Card> tripCards= new ArrayList<Card>(); // if it comes to check for two-pair, then it cannot have three-of-a-kind, but anyways no harm in checking.
		for(int i=0; i<allCards.size(); i++)	// do a deep copy to clone allCards.		
			tempCards.add(allCards.get(i));
		tripCards = isThreeKind();
		if(tripCards!=null)	// if there is a three of a kind, two pair doesnt count any more.
				return null;
		for(int i=0; i<tempCards.size()-1; i++)
		{
			if(tempCards.get(i).getValue() ==tempCards.get(i+1).getValue())
			{
				tempList.add(tempCards.get(i));
				tempCards.remove(i+1);
				tempCards.remove(i);
				i--;				
				numPairs++;
				if(numPairs==2)
				{
					if(tempCards.size()>=1)
						tempList.add(tempCards.get(tempCards.size()-1));
					// 3 cards are returned. 1st two are sorted pair cards and third is kicker. Eg. if hand is (4,4,6,6,J,K,A), then [4,6,A] is returned
					return tempList;
				}
			}
		}
		
		return null;
	}

	public List<Card> isPair() {
		
		this.getSorted();
		List<Card> tempList = new ArrayList<Card>();
		List<Card> tempCards= new ArrayList<Card>();
		List<Card> tripCards= new ArrayList<Card>();
		for(int i=0; i<allCards.size(); i++)	// do a deep copy to clone allCards.		
			tempCards.add(allCards.get(i));
		tripCards = isThreeKind();	// to check for a trip in case this method is being called from isFullHouse
		
		for (int i = tempCards.size() - 1; i >= 1; i--) {
			if(tripCards!=null){
				if(tempCards.get(i).getValue() == tripCards.get(0).getValue()){
					tempCards.remove(i);
					tempCards.remove(i-1);
					tempCards.remove(i-2);
					i = i-3;
				}
			}
			if (i>=1 && tempCards.get(i).getValue() == tempCards.get(i - 1).getValue()) {
				
				tempList.add(tempCards.get(i - 1));
				tempCards.remove(i);
				tempCards.remove(i-1);
				if(tripCards==null && tempCards.size()>=3){
					tempList.add(tempCards.get(tempCards.size()-3));
					tempList.add(tempCards.get(tempCards.size()-2));
					tempList.add(tempCards.get(tempCards.size()-1));
				}
				// 4 cards are returned, 1st is the pair card, last 3 are the kickers
				return tempList; // for a full-house, only the pair card is returned
			}
		}

		return null;
	}
	
	public List<Card> highCard() {
		// return best 5 cards. Eg (5,7,9,K,A)
		return allCards.subList(Math.max(allCards.size()-5,0), allCards.size());
	}
}
