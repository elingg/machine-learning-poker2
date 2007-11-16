
package edu.stanford.cs229.ml;

import java.util.Hashtable;


import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Card;
import edu.stanford.cs229.PlayerAction;
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
	
	private int numGames;
	
	private Util util;
	
	public ReinforcementLearningPlayer()
	{
		super();
		initialState= new Hashtable<Integer,Fraction>();
		fiveCardState= new Hashtable<Integer,Fraction>();
		sixCardState= new Hashtable<Integer,Fraction>();
		sevenCardState= new Hashtable<Integer,Fraction>();
		initialKey=-1;
		fiveCardKey=-1;
		sixCardKey=-1;
		sevenCardKey=-1;
		util=new Util();
	}
	
	private void initializeValues(Hashtable ht)
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
			initialKey=util.computeValue(hand);
		}
	}
	
	public void addTableCard(Card card)
	{
		super.addTableCard(card);
		if(hand.getPlayerCards().size()==3)
		{
			fiveCardKey=util.computeValue(hand);
		}
		else if (hand.getPlayerCards().size()==4)
		{
			sixCardKey=util.computeValue(hand);
		}
		else if(hand.getPlayerCards().size()==5)
		{
			sevenCardKey=util.computeValue(hand);
			
		}
	}
	
	private void updateValues()
	{
		Fraction initialVal= (Fraction)initialState.get(initialKey);
		Fraction fiveCardVal=(Fraction)fiveCardState.get(fiveCardKey);
		Fraction sixCardVal=(Fraction)sixCardState.get(sixCardKey);
		Fraction sevenCardVal=(Fraction)sevenCardState.get(sevenCardKey);
		//if game won
		initialState.put(initialKey, new Fraction(initialVal.getNumerator()+1, initialVal.getDenominator()+1));
		fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator()+1,fiveCardVal.getDenominator()+1));
		sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator()+1,sixCardVal.getDenominator()+1));
		sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator()+1,sevenCardVal.getDenominator()+1));
		//else
		initialState.put(initialKey, new Fraction(initialVal.getNumerator(), initialVal.getDenominator()+1));
		fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator(),fiveCardVal.getDenominator()+1));
		sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator(),sixCardVal.getDenominator()+1));
		sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator(),sevenCardVal.getDenominator()+1));
	
			
	}
	
	private float findExpectedValue(int key,Hashtable<Integer,Fraction> state, int amount)
	{
		Fraction val=(Fraction)state.get(key);
		float prob= val.getNumerator()/val.getDenominator();
		return (amount*(prob) - amount*(1-prob));
	}
	
	
	@Override
	public PlayerAction getAction() throws ApplicationException {
		// TODO Auto-generated method stub
		return null;
	}

}
