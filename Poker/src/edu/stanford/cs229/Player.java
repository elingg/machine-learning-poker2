package edu.stanford.cs229;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents a human player that will be playing through the Java console
 * @author ago
 * TODO: Rename to HumanPlayer.java
 */
public class Player extends AbstractPlayer {

	public Player(String name) {
		super(name);
	}
	
	public PlayerAction getAction(GameState state) throws ApplicationException {
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader stdin = new BufferedReader(isr);
			
			while (true) {
				System.out.print("(c)heck/(f)old/(b)et?");
				String actionType = stdin.readLine();
				if (actionType.indexOf("c") != -1) {
					return new PlayerAction(ActionType.CHECK_CALL, -1);
				} else if (actionType.indexOf("b") != -1) {
					/* Uncomment the following to allow players to bet an amount
					System.out.println("How much?");
					int bet = Integer.parseInt(stdin.readLine());
					*/					
					//For now, we always bet $10
					int bet = 10;
					return new PlayerAction(ActionType.BET_RAISE, bet);
				} else if (actionType.indexOf("f") != -1) {
					return new PlayerAction(ActionType.FOLD, -1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	public static boolean isDone() {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader stdin = new BufferedReader(isr);
		System.out.print("Player again? (Y/N)");
		try {
			String response = stdin.readLine();
			if ((response.indexOf("n") != -1)
					|| (response.indexOf("N") != -1)) {
				logger.fine("Ending game");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
