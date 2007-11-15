package edu.stanford.cs229;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Player extends AbstractPlayer {
	public PlayerAction getAction() {
		
	    InputStreamReader isr = new InputStreamReader( System.in );
	    BufferedReader stdin = new BufferedReader( isr );
	    System.out.print("Call/Fold/Bet?");
	    String actionType = stdin.readLine();
	    if(actionType.indexOf("c") != -1) {
	    	return new PlayerAction(ActionType.CHECK, 1);
	    } else if(actionType.indexOf("b") != -1) {
	    	return new PlayerAction(ActionType.BET, 1);
	    } else if(actionType.indexOf("f") != -1) {
	    	return new PlayerAction(ActionType.FOLD, 1);
	    }
	    
	    	
	}
}
