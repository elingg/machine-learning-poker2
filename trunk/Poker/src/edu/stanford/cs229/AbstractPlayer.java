package edu.stanford.cs229;

/**
 * This is a base class.  
 * @author ago
 *
 */
public abstract class AbstractPlayer {
	private final Hand hand;
	
	public AbstractPlayer() {
		hand = new Hand();
	}
	
	public void addCard(Card card) {
		hand.addCard(card);
	}

	public abstract PlayerAction getAction();
}
