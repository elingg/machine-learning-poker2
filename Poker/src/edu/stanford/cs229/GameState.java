package edu.stanford.cs229;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a game.  I'm not sure if I have the right abstraction here.
 * @author ago
 *
 */
public class GameState {
	private final List<Card> tableCards;
	private final Deck deck;
	
	public GameState() {
		tableCards = new ArrayList<Card>();
		deck = new Deck();
	}
	
	public void run() {
		Player player = new Player();
		Dealer dealer = new Dealer();
		
	    System.out.println("Starting game!");
		
		//Assign cards to player
		Card playerCard1 =  deck.drawCard();
		Card playerCard2 =  deck.drawCard();
		
		player.addCard(playerCard1);
		player.addCard(playerCard2);
		
		//Assign cards to player
		Card dealerCard1 = deck.drawCard();
		Card dealerCard2 = deck.drawCard();
		
		dealer.addCard(dealerCard1);
		dealer.addCard(dealerCard2);
		
		//Assign cards to table
		Card tableCard1 = deck.drawCard();
		tableCards.add(tableCard1);
		System.out.println("Drew card: " + tableCard1);
		
		player.getAction();
		
		Card tableCard2 = deck.drawCard();
		tableCards.add(tableCard2);
		System.out.println("Drew card: " + tableCard2);
				
		Card tableCard3 = deck.drawCard();
		tableCards.add(tableCard2);
		System.out.println("Drew card: " + tableCard2);
		
		Card tableCard4 = deck.drawCard();
		tableCards.add(tableCard2);
		System.out.println("Drew card: " + tableCard2);
		
		Card tableCard5 = deck.drawCard();
		tableCards.add(tableCard2);
		System.out.println("Drew card: " + tableCard2);
		
	}
	
}
