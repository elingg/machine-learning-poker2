package edu.stanford.cs229.web;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.PlayerAction;

/**
 * Whenever the GameEngine interacts with this class, we have to set the
 * "isTurnSignal" to true, so that the servlet knows it can proceed.
 * 
 * @author ago
 * 
 */
public class WebPlayer extends AbstractPlayer implements Serializable {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.web.WebPlayer");
	
	private final BlockingQueue<PlayerAction> playerActionQueue;
	private final BlockingQueue<Boolean> playAgainQueue;
	private final BlockingQueue<Integer> servletQueue;
	
	public WebPlayer(String name, String id, BlockingQueue<PlayerAction> webplayerQueue, BlockingQueue<Boolean> playAgainQueue, BlockingQueue<Integer> servletQueue) {
		super(name, id);
		this.playerActionQueue = webplayerQueue;
		this.playAgainQueue = playAgainQueue;
		this.servletQueue = servletQueue;
	}
	
	public BlockingQueue<Boolean> getPlayAgainQueue() {
		return playAgainQueue;
	}

	public BlockingQueue<PlayerAction> getWebplayerQueue() {
		return playerActionQueue;
	}

	public BlockingQueue<Integer> getServletQueue() {
		return servletQueue;
	}
	
	/**
	 * PubSub model
	 */
	public PlayerAction getAction(GameState state) throws ApplicationException {
		try {
			servletQueue.put(new Integer(1));
			return playerActionQueue.take();
		} catch(InterruptedException e) {
			e.printStackTrace();
			throw new ApplicationException("Thread has ended");
		}
	}

	/**
	 * TODO: Fix this.
	 */
	public boolean isDonePlaying() {
		try {
			servletQueue.put(new Integer(1));
			playAgainQueue.take();
			return false;
		} catch(InterruptedException e) {
			e.printStackTrace();
			//TODO: Fix this.  I'm not sure what's supposed to happen here.
			return false;
		}
		
	}

	
}
