package edu.stanford.cs229.web;

import java.io.Serializable;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Constants;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.PlayerAction;

public class WebPlayer extends AbstractPlayer implements Serializable {
	int SLEEP_DELAY = 500; //in ms
	int MAX_ATTEMPTS = 1000;
	
	private String decisionSignal = null;
	private PlayerAction currentAction = null;
	private boolean isTurn;
	
	public WebPlayer(String name) {
		super(name);
	}
	
	/**
	 * PubSub model
	 */
	public PlayerAction getAction(GameState state) throws ApplicationException {
		this.isTurn = true;

		try {
			int count = 0;
			while(currentAction == null && count < MAX_ATTEMPTS) {
				count++;
				//logger.finest("Did not find action for " + name);
				Thread.sleep(SLEEP_DELAY);
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		PlayerAction action = new PlayerAction(currentAction.getActionType(), currentAction.getBet()); 
		this.currentAction = null;
		this.isTurn = false;
		return action;
	}

	public void setCurrentAction(PlayerAction currentAction) {
		this.currentAction = currentAction;
	}
	
	public PlayerAction getCurrectAction() {
		return currentAction;
	}

	public boolean isTurn() {
		return isTurn;
	}

	public void setTurn(boolean isTurn) {
		this.isTurn = isTurn;
	}
	
	public boolean isDonePlaying() {
		System.out.println("Entering isDonePlaying");
		
		int count = 0;
		try {
			while (decisionSignal == null && count < MAX_ATTEMPTS) {
				count++;
				Thread.sleep(SLEEP_DELAY);
			}
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public String getDecisionSignal() {
		return decisionSignal;
	}

	public void setDecisionSignal(String decisionToContinue) {
		this.decisionSignal = decisionToContinue;
	}

}
