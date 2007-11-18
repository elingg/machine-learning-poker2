
package edu.stanford.cs229.ml;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ActionType;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Card;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.ResultState;
import edu.stanford.cs229.Util;

public class ReinforcementLearningPlayer extends AbstractPlayer {

	private Hashtable<Integer,Fraction> initialState;
	private Hashtable<Integer,Fraction> fiveCardState;
	private Hashtable<Integer,Fraction> sixCardState;
	private Hashtable<Integer,Fraction> sevenCardState;
	
	private int initialKey;
	private int fiveCardKey;
	private int sixCardKey;
	private int sevenCardKey;
	
	private int numRounds;
	
	
	public ReinforcementLearningPlayer(String name)
	{
		super(name);
		initialState= new Hashtable<Integer,Fraction>();
		fiveCardState= new Hashtable<Integer,Fraction>();
		sixCardState= new Hashtable<Integer,Fraction>();
		sevenCardState= new Hashtable<Integer,Fraction>();
		initialKey=-1;
		fiveCardKey=-1;
		sixCardKey=-1;
		sevenCardKey=-1;
		initializeValues(initialState);
		initializeValues(fiveCardState);
		initializeValues(sixCardState);
		initializeValues(sevenCardState);
		numRounds=0;
		
	}
	
	private void initializeValues(Hashtable<Integer,Fraction> ht)
	{
		for(int i=0; i<=800; i=i+100)
		{
			for(int j=2; j<=14; j++)
			{
				ht.put((i+j), new Fraction(1,2));
			}
		}
	}
	
	public void addPlayerCard(Card card)
	{
		
		super.addPlayerCard(card);
		if(hand.getPlayerCards().size()==2)
		{
			initialKey=Util.computeValue(hand);
		}
	}
	
	public void addTableCard(Card card)
	{
		super.addTableCard(card);
		if(hand.getTableCards().size()==3)
		{
			fiveCardKey=Util.computeValue(hand);
		}
		else if (hand.getTableCards().size()==4)
		{
			sixCardKey=Util.computeValue(hand);
		}
		else if(hand.getTableCards().size()==5)
		{
			sevenCardKey=Util.computeValue(hand);
			
		}
	}
	
	/**
	 * Finalizes all talbe updates after a game ends.  Also, clears the hand for the next round.
	 * TODO: Rename
	 */
	public void processEndOfGame(int resultState)
	{
		logger.finest("Am I called? hand");
		Fraction initialVal= (Fraction)initialState.get(initialKey);
		Fraction fiveCardVal=(Fraction)fiveCardState.get(fiveCardKey);
		Fraction sixCardVal=(Fraction)sixCardState.get(sixCardKey);
		Fraction sevenCardVal=(Fraction)sevenCardState.get(sevenCardKey);
		
		
		if(resultState == ResultState.WIN) {
			initialState.put(initialKey, new Fraction(initialVal.getNumerator()+1, initialVal.getDenominator()+1));
			fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator()+1,fiveCardVal.getDenominator()+1));
			sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator()+1,sixCardVal.getDenominator()+1));
			sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator()+1,sevenCardVal.getDenominator()+1));
		} else if(resultState == ResultState.LOSE) {
			initialState.put(initialKey, new Fraction(initialVal.getNumerator(), initialVal.getDenominator()+1));
			fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator(),fiveCardVal.getDenominator()+1));
			sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator(),sixCardVal.getDenominator()+1));
			sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator(),sevenCardVal.getDenominator()+1));
		}
		numRounds++;
		logger.finest("Before Clearing hand");
		super.processEndOfGame(resultState);
			
	}
	
	private float findExpectedValue(int key,Hashtable<Integer,Fraction> state, int amount)
	{
		Fraction val=(Fraction)state.get(key);
		float prob= val.getNumerator()/val.getDenominator();
		return (amount*(prob) - amount*(1-prob));
	}
	
	
	@Override
	public PlayerAction getAction(GameState state) throws ApplicationException {
		if(this.hand.getAllCards().size() == 2) 
			return getActionAfterNumCards(this.initialState);
		
		else if (this.hand.getAllCards().size()==5)
			return getActionAfterNumCards(this.fiveCardState);
		
		else if (this.hand.getAllCards().size()==6)
			return getActionAfterNumCards(this.sixCardState);
		else if (this.hand.getAllCards().size()==7)
			return getActionAfterNumCards(this.sevenCardState);

		//throw new ApplicationException(this.hand.getAllCards().size() + "is not a valid value");
		System.err.println(this.hand.getAllCards().size() + "is not a valid value");
		return null;
		
	}
	
	public PlayerAction getActionAfterNumCards(Hashtable<Integer, Fraction> ht)
	{
		float exp=findExpectedValue(Util.computeValue(this.hand),ht, this.getPot());
		float expbet= findExpectedValue(Util.computeValue(this.hand), ht, this.getPot()+10);
		float ev= Math.max(exp, expbet);
		if(ev<0 && numRounds>100)
			return new PlayerAction(ActionType.FOLD, 0);
		else if(ev==exp)
			return new PlayerAction(ActionType.CHECK,0);
		else
			return new PlayerAction(ActionType.BET,10);
		
	}
	
	public void debugResults()
	{
		debugState(this.initialState);
		debugState(this.fiveCardState);
		debugState(this.sixCardState);
		debugState(this.sevenCardState);

		System.out.println();
	}
	
	public void debugState(Hashtable<Integer, Fraction> ht) {
		
		
		for(int i=0; i<=800; i=i+100)
		{
			for(int j=2; j<=14; j++)
			{
				System.out.println((i+j)+ " " + ht.get(i+j));
			}
		}
		
		/*Integer i;
		Set<Integer> set = ht.keySet();
	    Iterator<Integer> itr = set.iterator();
	    while (itr.hasNext()) {
	        i = itr.next();
	        System.out.println(i + ": " + ht.get(i));
	    }*/

	}

}
