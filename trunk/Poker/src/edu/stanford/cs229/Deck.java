package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a deck(s) of cards (4)
 * 
 * @author ago
 * 
 */
public class Deck {
	private final int NUM_DECKS = 1; // Number of decks

	public final List<Card> cardsDrawn; // History of cards drawn

	Logger logger = Logger.getLogger("edu.stanford.cs229.Deck");

	public static void main(String[] args) {
		
	    List<Card> playerCards= new ArrayList<Card>();
		 List<Card> tableCards = new ArrayList<Card>();
		 List<Card> allCards = new ArrayList<Card>();
		Deck deck = new Deck();
		Hand hand= new Hand();
		/*for (int i = 0; i < 5; i++) {			
			tableCards.add(deck.drawCard());			
		}
		
		for(int i=0; i <2; i++){
			playerCards.add(deck.drawCard());
		}
		
		allCards.addAll(tableCards);
		allCards.addAll(playerCards);
		
		hand.setAllCards(allCards);
		hand.setPlayerCards(playerCards);
		hand.setTableCards(tableCards);*/
		
		/*
		Card c= new Card(6,1);
		Card c1= new Card(2,1);
		Card c2= new Card(3,1);
		Card c3= new Card(4,1);
		Card c4= new Card(5,1);
		Card c5= new Card(9,3);
		
		allCards.add(c);
		allCards.add(c1);
		allCards.add(c2);
		allCards.add(c3);
		allCards.add(c4);
		allCards.add(c5);
		hand.setAllCards(allCards);
		
		for(int i=0; i<6; i++)
		{
			System.out.println(allCards.get(i).toString());
		}
		
		List<Card> result= hand.isStraightFlush();
		if(result!=null)
		{
				System.out.println(result.toString());
		}
	*/
			
	}

	/**
	 * Constructor
	 * 
	 */
	public Deck() {
		cardsDrawn = new ArrayList<Card>();
	}

	/**
	 * Draws a random card
	 * 
	 * @return
	 */
	public Card drawCard() {
		Card card;
		do {
			card = new Card((int) (Math.random() * 12 + 2), (int) Math
					.floor(Math.random() * 4 + 1));

		} while (!isValid(card));
		cardsDrawn.add(card);
		return card;
	}

	/**
	 * Method to check that the randomly drawn card is valid
	 * 
	 * @param card
	 * @return
	 */
	private boolean isValid(Card card) {
		int duplicateCount = 0;
		for (Card c : cardsDrawn) {
			if (card.equals(c)) {
				duplicateCount++;
			}
		}
		
		if (duplicateCount >= NUM_DECKS) {
			
			return false;
		} else {
			return true;
		}
	}
}
