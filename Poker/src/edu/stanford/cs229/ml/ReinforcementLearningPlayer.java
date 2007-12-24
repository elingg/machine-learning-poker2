
package edu.stanford.cs229.ml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ActionType;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Card;
import edu.stanford.cs229.Deck;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.Hand;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.ResultState;
import edu.stanford.cs229.Util;

public class ReinforcementLearningPlayer extends AbstractPlayer implements Serializable {

	private Hashtable<Integer,Fraction> initialState;
	private Hashtable<Integer,Fraction> fiveCardState;
	private Hashtable<Integer,Fraction> sixCardState;
	private Hashtable<Integer,Fraction> sevenCardState;
	
	private int initialKey;
	private int fiveCardKey;
	private int sixCardKey;
	private int sevenCardKey;
	
	private int numGames;
	private int numGamesWon;
	private int betCountMaxForEachGame;
	
	private Fraction strengthOfOpponent;
	
	/*
	public ReinforcementLearningPlayer() {
		super();
	}
	*/
	
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
		numGames=0;
		strengthOfOpponent=new Fraction(1,2);
	}
	
	private void initializeValues(Hashtable<Integer,Fraction> ht)
	{
		for(int i=0; i<=800; i=i+100)
		{
			for(int j=0; j<=14; j++)
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
			strengthOfOpponent = new Fraction(strengthOfOpponent.getNumerator()+1, strengthOfOpponent.getDenominator()+1);
		} else if(resultState == ResultState.LOSE) {
			initialState.put(initialKey, new Fraction(initialVal.getNumerator(), initialVal.getDenominator()+1));
			fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator(),fiveCardVal.getDenominator()+1));
			sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator(),sixCardVal.getDenominator()+1));
			sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator(),sevenCardVal.getDenominator()+1));
			strengthOfOpponent = new Fraction(strengthOfOpponent.getNumerator(), strengthOfOpponent.getDenominator()+1);
		}
		numGames++;
		betCountMaxForEachGame = 0;
		logger.finest("Before Clearing hand");
		super.processEndOfGame(resultState);
			
	}
	

	
	
	@Override
	public PlayerAction getAction(GameState state) throws ApplicationException {
		
		if(this.hand.getAllCards().size() == 2) 
			return getActionAfterNumCards(this.initialState, state);
		else if (this.hand.getAllCards().size()==5)
			return getActionAfterNumCards(this.fiveCardState, state);
		else if (this.hand.getAllCards().size()==6)
			return getActionAfterNumCards(this.sixCardState,state);
		else if (this.hand.getAllCards().size()==7)
			return getActionAfterNumCards(this.sevenCardState,state);

		System.err.println(this.hand.getAllCards().size() + "is not a valid value");
		return null;		
	}
	
	/**
	 * Gets the player action, which is determined by the Hashtable of probabilities
	 * @param ht
	 * @return
	 */
	public PlayerAction getActionAfterNumCards(Hashtable<Integer, Fraction> ht, GameState state)
	{
		float FOLDING_THRESHOLD=(float).0;
		float CHECKING_THRESHOLD=(float).7;
		float exp=(float).5;
		Random rand= new Random();
		float fuzzy= rand.nextFloat();
		int totalPot = state.getTotalPot();
		float opponentBet = state.getOpponentBet(this);
		
		int handVal= Util.computeValue(this.hand);
		
		if(hand.getAllCards().size()>2)
		{
			FOLDING_THRESHOLD=(float).2;
			
			exp= 1-Util.computeOuts(hand);
			
			if(((float)(strengthOfOpponent.getNumerator()/ strengthOfOpponent.getDenominator()) >.6) || this.getBankroll() <=850)
				CHECKING_THRESHOLD = (float).8;
		}
		
		else
		{
		   exp=findExpectedValue(handVal,ht, this.getPot(), totalPot);
		

			if(opponentBet > 99 && exp < .7) //fold if opponent bet is too high
				return new PlayerAction(ActionType.FOLD, 0);
			
		}
		
		
		if((exp < FOLDING_THRESHOLD && opponentBet >0) || (totalPot + opponentBet > 180 && opponentBet >0 && exp < .45 && fuzzy > .2))
			return new PlayerAction(ActionType.FOLD, 0);
		
		else if(fuzzy <= .2 && exp >=.6) 
		{
			if (fuzzy > .15)
				return new PlayerAction(ActionType.BET_OR_RAISE, 10);
		    else if(fuzzy > .05)
			    return new PlayerAction(ActionType.BET_OR_RAISE,20);
			else
				return new PlayerAction(ActionType.BET_OR_RAISE,60);
		}
		
		else if(exp<=CHECKING_THRESHOLD || betCountMaxForEachGame > 6)
		    return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		
		else {
			betCountMaxForEachGame++;
			if(handVal>=600)
				return new PlayerAction(ActionType.BET_OR_RAISE,60);
			else
				return new PlayerAction(ActionType.BET_OR_RAISE,40);
		}
	}
	
	private float findExpectedValue(int key,Hashtable<Integer,Fraction> state, int amtLose, int amtWin)
	{
		Fraction val=(Fraction)state.get(key);
		float prob= ((float)val.getNumerator()/(float)val.getDenominator());
		
		//return (amtWin*(prob) - amtLose*(1-prob));
		return prob;
	}
	
	
	
	/**
	 * Writes results for each state hashtable
	 *i 
	 */
	public void debugResults()
	{
		debugState(this.initialState);
		debugState(this.fiveCardState);
		debugState(this.sixCardState);
		debugState(this.sevenCardState);
	}
	
	/**
	 * 
	 * @param ht
	 */
	public void debugState(Hashtable<Integer, Fraction> ht) {
		for(int i=0; i<=800; i=i+100)
		{
			for(int j=0; j<=14; j++)
			{
				System.out.println((i+j)+ " " + ht.get(i+j));
			}
		}
	}

}
