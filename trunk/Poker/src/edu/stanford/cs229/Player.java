package edu.stanford.cs229;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Represents a human player that will be playing through the Java console
 * @author ago
 *
 */
public class Player extends AbstractPlayer {
	public PlayerAction getAction() throws ApplicationException {
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader stdin = new BufferedReader(isr);
			System.out.print("Call/Fold/Bet?");
			String actionType = stdin.readLine();
			if (actionType.indexOf("c") != -1) {
				return new PlayerAction(ActionType.CHECK, -1);
			} else if (actionType.indexOf("b") != -1) {
				System.out.println("How much?");
				int bet = Integer.parseInt(stdin.readLine());
				return new PlayerAction(ActionType.BET, bet);
			} else if (actionType.indexOf("f") != -1) {
				return new PlayerAction(ActionType.FOLD, -1);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
		//TODO: Clean up logic
		return null;
	}
}
