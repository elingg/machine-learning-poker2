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
		List<Card> tempList = this.isStraight();
		List<Card> tempList1 = this.isFlush();
		if (tempList != null && tempList1 != null && tempList.equals(tempList1)) {
			return tempList;
		}
		return null;
	}
	
	
	public List<Card> isFlush(){
		
		List<Card> tempList=null;
		
		this.getSorted();
		for(int i=1; i<=4; i++)
		{
			List<Card> suitList= getSuit(i);
			if(suitList.size()>=5)
			{
				suitList= suitList.subList(suitList.size()-5, suitList.size());
				
				if(tempList==null)
				{
					tempList=suitList;
				}
				else if(suitList.get(4).getValue() > tempList.get(4).getValue())
				{
					tempList=suitList;
				}
			}
			
		}
		return tempList;
	}
	
	
	public List<Card> isStraight(){
		
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		
		for(int i=allCards.size()-1; i>=4; i--)
		{
			
			if(((allCards.get(i)).getValue() - 1 == (allCards.get(i-1)).getValue()) && ((allCards.get(i-1)).getValue() - 1 == (allCards.get(i-2)).getValue()) && ((allCards.get(i-2)).getValue() - 1 == (allCards.get(i-3)).getValue()) && ((allCards.get(i-3)).getValue() - 1 == (allCards.get(i-4)).getValue()))
			{
				tempList.add(allCards.get(i-4));
				tempList.add(allCards.get(i-3));
				tempList.add(allCards.get(i-2));
				tempList.add(allCards.get(i-1));
				tempList.add(allCards.get(i));
				
				return tempList;
			}
				
		}
		
		return null;
		
	}
	
	
	public List<Card> isFullHouse(){
		
		//sort all cards
		List<Card> tempList= this.isPair();
		List<Card> tempList1 = this.isThreeKind();
		
		if(tempList != null && tempList1 !=null && !tempList.get(tempList.size()-1).equals(tempList1.get(tempList1.size()-1)))
		{
			tempList.addAll(tempList1);
			Collections.sort(tempList);
			return tempList;
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
				tempList.add(allCards.get(i-2));
				tempList.add(allCards.get(i-1));
				tempList.add(allCards.get(i));
				return tempList;
			}
				
		}
		
		return null;
		
	}
	
	public List<Card> isThreeKind(){
		
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		
		for(int i=allCards.size()-1; i>=2; i--)
		{
			if(allCards.get(i).getValue() ==allCards.get(i-1).getValue() && allCards.get(i-1).getValue() ==allCards.get(i-2).getValue() )
			{
				tempList.add(allCards.get(i-2));
				tempList.add(allCards.get(i-1));
				tempList.add(allCards.get(i));
				return tempList;
			}
		}
		
		return null;
	}
	
	public List<Card> isTwoPair()
	{
		int numPairs=0;
		
		this.getSorted();
		List<Card> tempList= new ArrayList<Card>();
		
		for(int i=allCards.size()-1; i>=1; i--)
		{
			if(allCards.get(i).getValue() ==allCards.get(i-1).getValue())
			{
				tempList.add(allCards.get(i-1));
				tempList.add(allCards.get(i));
				
				numPairs++;
				if(numPairs==2)
				{
					Collections.sort(tempList);
					return tempList;
				}
			}
		}
		
		return null;
		
	}

	public List<Card> isPair() {
		//sort all cards
		this.getSorted();
		List<Card> tempList = new ArrayList<Card>();

		for (int i = allCards.size() - 1; i >= 1; i--) {
			if (allCards.get(i).getValue() == allCards.get(i - 1).getValue()) {
				tempList.add(allCards.get(i - 1));
				tempList.add(allCards.get(i));
				return tempList;
			}
		}

		return null;
	}
}
