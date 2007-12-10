package edu.stanford.cs229;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

/**
 * This class represents a BPP simulator
 * @author ago
 *
 */
public class BppSimulator {
	public static void main(String args[]) throws Exception {
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();
		HumanPlayer player1 = new HumanPlayer("BPP");
		//ReinforcementLearningPlayer player2 = new ReinforcementLearningPlayer("Computer");
		ReinforcementLearningPlayer player2 = deserializePlayer("Computer");
		players.add(player1);
		players.add(player2);
		BppDeck bppDeck = new BppDeck();
		Game game = new Game(players, bppDeck);
		game.run();
	}
	
	/**
	 * Deserializes a ReinforcementLearnerPlayer
	 * @param name
	 * @return
	 * @throws ApplicationException
	 */
	private static ReinforcementLearningPlayer deserializePlayer(String name) throws ApplicationException {
		ObjectInput input = null;
		try {
	      InputStream file = new FileInputStream(name);
	      InputStream buffer = new BufferedInputStream(file);
	      input = new ObjectInputStream ( buffer );
	      ReinforcementLearningPlayer player = (ReinforcementLearningPlayer) input.readObject();
	      return player;
		} catch(IOException e) {
			throw new ApplicationException(e);
		} catch(ClassNotFoundException e) {
			throw new ApplicationException(e);
		}		
	}
}
