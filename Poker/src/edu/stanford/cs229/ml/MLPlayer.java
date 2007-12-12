
package edu.stanford.cs229.ml;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Random;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ActionType;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Card;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.ResultState;
import edu.stanford.cs229.Util;

public class MLPlayer extends AbstractPlayer implements Serializable {

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
	
 Fraction strengthOfOpponent;
	
	/*
	public Player2() {
		super();
	}
	*/
	
	public MLPlayer(String name)
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
			ht.put((i), new Fraction(1,2));
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
			if(fiveCardVal !=null)
				fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator()+1,fiveCardVal.getDenominator()+1));
			if(sixCardVal !=null)
				sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator()+1,sixCardVal.getDenominator()+1));
			if(sevenCardVal !=null)
				sevenCardState.put(sevenCardKey,new Fraction(sevenCardVal.getNumerator()+1,sevenCardVal.getDenominator()+1));
			strengthOfOpponent = new Fraction(strengthOfOpponent.getNumerator()+1, strengthOfOpponent.getDenominator()+1);
		} else if(resultState == ResultState.LOSE) {
			initialState.put(initialKey, new Fraction(initialVal.getNumerator(), initialVal.getDenominator()+1));
			if(fiveCardVal !=null)
				fiveCardState.put(fiveCardKey, new Fraction(fiveCardVal.getNumerator(),fiveCardVal.getDenominator()+1));
			if(sixCardVal !=null)
				sixCardState.put(sixCardKey, new Fraction(sixCardVal.getNumerator(),sixCardVal.getDenominator()+1));
			if(sevenCardVal !=null)
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
		
		// room for heuristic fn. to be calculated from analyzed data..
		float T1 = (float)0.1;	// thresholds to be altered from learnt data
		float T2 = (float)0.25;
		float T3 = (float)0.5;
		float T4 = (float)0.72;
		float T5 = (float)0.89;
		float T6 = (float)0.96;	// sure win
		float Pwin;
		float optimumBet;
		int maxBet = 0;
		int totalPot = state.getTotalPot();
		int Bankroll = this.getBankroll();
		
		boolean isBluff = false;
		boolean isBigBluff = false;
		boolean isFold = false;
		boolean isBigBlind = false;
		boolean isSlowPlay = false;
		
		float opponentBet = state.getOpponentBet(this);
		float randValue;
		Random rand= new Random();
		
		Pwin = Util.computeWinProbability(hand); // can compute this for every stage
		
		if (Bankroll <0)
			Bankroll = 1000;
		
		optimumBet = ( ( (float) totalPot * (float) Bankroll * (float) Pwin ) / ( (float) totalPot + (float) Bankroll - (float) Pwin * (float) Bankroll ) );
		maxBet = (int) optimumBet / 4;
		if(maxBet < 10)
			maxBet = 10;
		else maxBet = maxBet - maxBet%10;
		
		if(state.isBigBlind(this))	// if I am the big-blind
			isBigBlind = true;
		
		randValue = rand.nextFloat();
		if(randValue <=0.2)	// bluffs with a probability of 0.2 at random
			isBluff = true;
		if(randValue <=0.02)
			isBigBluff = true;
		if(randValue <=0.7)
			isSlowPlay = true;
		if(opponentBet == 0)
			isFold = false;
		
		
		//Bluff or No Bluff, then stage of the game..
		
		if(Bankroll < 200 && Pwin >=T3){
			if(opponentBet >= Bankroll)
				return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			else return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(Bankroll-(int)opponentBet, Bankroll));
		}
		
		if(this.hand.getAllCards().size() ==2 && Pwin > T4 && !isSlowPlay){
			if(maxBet>opponentBet)
				return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}			
		if(this.hand.getAllCards().size() == 2 && Pwin > T5 && !isSlowPlay){
			if(maxBet>opponentBet)
				return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, 2*maxBet));
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}
		if(Pwin <= T1){
			if(this.hand.getAllCards().size() == 2 && !isBluff){		// fold very bad hands pre-flop
				if(isBigBlind == false)
					return new PlayerAction(ActionType.FOLD, 0);
				else if(opponentBet >0)
					return new PlayerAction(ActionType.FOLD, 0);
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			if(!isBluff){
				if(opponentBet >=10)
					return new PlayerAction(ActionType.FOLD, 0);
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			if(isBigBluff){
				if(maxBet>opponentBet)
					return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			else if(opponentBet > Bankroll/20)
				return new PlayerAction(ActionType.FOLD, 0);
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}
		  
		if(Pwin <=T2){
			if(isFold == true && !isBluff)
				return new PlayerAction(ActionType.FOLD, 0);
			else if(opponentBet == 0 && !isBluff)
				return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			if(isBigBluff){
				if(maxBet>opponentBet)
					return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			if(!isBluff){
				if(opponentBet >=10)
					return new PlayerAction(ActionType.FOLD, 0);
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			if(isBigBluff){
				if(maxBet>opponentBet)
					return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			else if(opponentBet > Bankroll/20)
				return new PlayerAction(ActionType.FOLD, 0);
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}

		if(Pwin <=T3){
			if(opponentBet>= maxBet && !isBluff && isFold)
				return new PlayerAction(ActionType.FOLD, 0);
			if(!isBluff){
				if(opponentBet >= maxBet && isFold)
					return new PlayerAction(ActionType.FOLD, 0);
				else
					return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			if(isBigBluff){
				if(maxBet>opponentBet)
					return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			else if(opponentBet > Bankroll/10)
				return new PlayerAction(ActionType.FOLD, 0);
			else {
			if(maxBet>opponentBet)
				return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
		}

		if(Pwin <=T4){
			if(opponentBet>= maxBet+20 && !isBluff && isFold)
				return new PlayerAction(ActionType.FOLD, 0);
			if(!isBluff){
				if(opponentBet >= maxBet+20 && isFold)
					return new PlayerAction(ActionType.FOLD, 0);
				else
					return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			else if(opponentBet > Bankroll/8 && opponentBet >= maxBet + 20)
				return new PlayerAction(ActionType.FOLD, 0);
			else {
				if(maxBet>opponentBet)
					return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
		}
		
		if(Pwin <=T5){
			if(opponentBet>= maxBet+50 && !isSlowPlay && isFold)
				return new PlayerAction(ActionType.FOLD, 0);
			if(!isSlowPlay || hand.getAllCards().size()==7){
				
				if(opponentBet <= maxBet+100){
					if(maxBet>opponentBet){
						if(isBluff)
							return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
						else return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(maxBet-(int)opponentBet, maxBet));
					}
					else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
				}
				else return new PlayerAction(ActionType.FOLD, 0);
			}
			else if(opponentBet > Bankroll/4 && opponentBet >= maxBet + 100)
				return new PlayerAction(ActionType.FOLD, 0);
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}
		
		if(Pwin <T6){
			if(opponentBet>= maxBet+200 && !isSlowPlay && isFold)
				return new PlayerAction(ActionType.FOLD, 0);
			if(!isSlowPlay || hand.getAllCards().size()==7){
				if(opponentBet <= maxBet+250){
					if(maxBet*2>opponentBet){
						if(isBluff)
							return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(2*maxBet-(int)opponentBet, 2*maxBet));
						else return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(2*maxBet-(int)opponentBet, 2*maxBet));
					}
					else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
				}
				else return new PlayerAction(ActionType.FOLD, 0);
			}
			else if(opponentBet > Bankroll/2 && opponentBet >= maxBet + 350)
				return new PlayerAction(ActionType.FOLD, 0);
			else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		}
		else{ // Pwin >T6
			if(!isSlowPlay || hand.getAllCards().size()==7){
				if(maxBet*3>opponentBet){
					if(isBluff)
						return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(3*maxBet-(int)opponentBet, 3*maxBet));
					else return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(3*maxBet-(int)opponentBet, 3*maxBet));
				}
				else return new PlayerAction(ActionType.CHECK_OR_CALL,0);
			}
			else return new PlayerAction(ActionType.BET_OR_RAISE,Math.max(3*maxBet-(int)opponentBet, 3*maxBet));
		}
			
	/*	
		float FOLDING_THRESHOLD=(float).05;
		float CHECKING_THRESHOLD=(float).75;
		float exp=(float).5;
		
		float fuzzy= rand.nextFloat();
		int totalPot = state.getTotalPot();
	
		
		int handVal= Util.computeValue(this.hand);
		
		
		//System.out.println("Prob:"+exp);
		
		if(hand.getAllCards().size()>=6)
		{
			FOLDING_THRESHOLD=(float).2;
			CHECKING_THRESHOLD=(float).7;
			
			
			//exp= 1-Util.computeOuts(hand);
			//System.out.println("Outs:"+exp);
			
			
			if(((float)(strengthOfOpponent.getNumerator()/ strengthOfOpponent.getDenominator()) >.6) || this.getBankroll() <=850)
				CHECKING_THRESHOLD = (float).8;
		}
		
		else
		{

		FOLDING_THRESHOLD = (float).05;
		
		if(this.hand.getAllCards().size() ==2)
			FOLDING_THRESHOLD = (float)0;
		
		CHECKING_THRESHOLD = (float).75;
		
		exp=findExpectedValue(handVal,ht, this.getPot(), totalPot);
		
		if(((float)(strengthOfOpponent.getNumerator()/ strengthOfOpponent.getDenominator()) >.6) || this.getBankroll() <=850)
			CHECKING_THRESHOLD = (float).9;
		
		}
		
		if(opponentBet > 99 && exp < .9)
			return new PlayerAction(ActionType.FOLD, 0);
		
		else if(exp < FOLDING_THRESHOLD || (totalPot + opponentBet > 100 && exp < .7 && fuzzy > .2))
			return new PlayerAction(ActionType.FOLD, 0);
		
		else if(exp<=CHECKING_THRESHOLD || betCountMaxForEachGame > 3 )
		    return new PlayerAction(ActionType.CHECK_OR_CALL,0);
		
		else if(fuzzy < .31 && exp >.5)
		{
			if(fuzzy<.10)
			    return new PlayerAction(ActionType.BET_OR_RAISE,30);
			else
				return new PlayerAction(ActionType.BET_OR_RAISE,10);
		}
		
		else {
			betCountMaxForEachGame++;
			if(handVal>=600)
				return new PlayerAction(ActionType.BET_OR_RAISE,60);
			else
				return new PlayerAction(ActionType.BET_OR_RAISE,30);
		}
		*/
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
			System.out.println(i+ " " + ht.get(i));
		}
	}

}
