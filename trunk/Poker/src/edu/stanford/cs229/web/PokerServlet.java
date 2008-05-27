package edu.stanford.cs229.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ActionType;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.Constants;
import edu.stanford.cs229.Game;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

public class PokerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static Logger logger = Logger.getLogger("edu.stanford.cs229.web.PokerServlet");
	
	private static String DEFAULT_BOT_CLASS = "ReinforcementLearningPlayer";
	private static String DEFAULT_BOT_PACKAGE = "edu.stanford.cs229.ml";
	
	//Get whether to deserialize the bot from disk.
	private static boolean DESERIALIZE_BOT = true;
	
	public static final int MAX_BET = 1000;
	
	private List<Game> activeGames = new ArrayList<Game>();
	private static final long GAME_TIMEOUT = 1000 * 60 * 30; //30 minutes

	/**
	 * Constructor
	 */
	public PokerServlet() {
		super();
	}
	
	/**
	 * Processes POST request.  This web application does not accept GET requests.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	/**
	 * Main servlet for the poker servlet.  This method is responsible for a few tasks:
	 * <ul>
	 * <li>Setting up a new game thread if it doesn't exist</li>
	 * <li>Processing a player action</li>
	 * <li>Processing a "play again" action</li>
	 * </ul>
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			HttpSession session = request.getSession(true);

			//Set up a new game, if there is no Game object in the seesion
			if (session.getAttribute(Constants.GAME_ATTRIBUTE) == null) {
				
				Object isFacebook = request.getAttribute(Constants.IS_FACEBOOK_ATTRIBUTE);
				if(isFacebook == null) {
					//Non-Facebook

					String name = request.getParameter(Constants.NAME_PARAMETER);
					String id = name + request.getRemoteAddr() + DEFAULT_BOT_CLASS;
					
					logger.info("Starting non-Facebook Game");
					processNewGame(id, name, DEFAULT_BOT_CLASS, DESERIALIZE_BOT, session);
				} else {
					//Facebook
					
					String name = (String) request.getAttribute(Constants.NAME_PARAMETER);
					String id = ((Integer) request.getAttribute(Constants.ID_PARAMETER)).toString() + DEFAULT_BOT_CLASS;

					logger.info("Starting Facebook Game.  Name: [" + name + "] ID: [" + id + "]");
					processNewGame(id, name, DEFAULT_BOT_CLASS, DESERIALIZE_BOT, session);
				}
			} 
			
			WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER_ATTRIBUTE);

			//Process the player actions
			String actionStr = request.getParameter(Constants.ACTION_TYPE_PARAMETER);
			String bet = request.getParameter(Constants.BET_PARAMETER);
			
			//TODO: Push this down to Game.java
			if(actionStr != null && !actionStr.equals("")) {
				Long responseTime = System.currentTimeMillis() - (Long) session.getAttribute(Constants.TIMESTAMP_ATTRIBUTE);
				processPlayerAction(player, actionStr, bet, responseTime);
			}

			//Process "play again" signal that happens at the end of a game
			String playAgain = request.getParameter(Constants.PLAY_AGAIN_PARAMETER);
			if(playAgain != null && !playAgain.equals("")) {
				player.getPlayAgainQueue().add(Boolean.TRUE);
			}
			
			player.getServletQueue().take();	
			
			session.setAttribute(Constants.TIMESTAMP_ATTRIBUTE, new Long(System.currentTimeMillis()));
			request.getRequestDispatcher("WEB-INF/game.jsp").forward(request, response);
			
		} catch (InterruptedException e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Sets up a new game
	 * @param playerId
	 * @param playerName
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void processNewGame(String playerId, String playerName, String botClassName, boolean deserializeBot, HttpSession session) throws ServletException, IOException {
		//Create game if it has not been set up for this session
		
		if(playerName == null || playerName.equals("")) {
			playerName = "Guest";
		}
		
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();

		AbstractPlayer player1 = null;
		if (deserializeBot) {
			//Deserialize the bot from disk
			player1 = getComputerPlayer(botClassName);
			player1.setName(Constants.WEBAPP_OPPONENT_NAME);

		} else {
			//Instantiate a new class dynamically
			String fullClassName = DEFAULT_BOT_PACKAGE + "." + botClassName;
			try {
				player1 = Game.generatePlayer(fullClassName, botClassName);
			} catch (ApplicationException e) {
				throw new ServletException(e);
			}
		}
		player1.setId("Computer-" + playerId);
		players.add(player1);
		BlockingQueue<PlayerAction> playerActionQueue = new LinkedBlockingQueue<PlayerAction>();
		BlockingQueue<Boolean> playAgainQueue = new LinkedBlockingQueue<Boolean>();
		BlockingQueue<Integer> servletQueue = new LinkedBlockingQueue<Integer>();
		WebPlayer player2 = new WebPlayer(playerName, playerId, playerActionQueue, playAgainQueue, servletQueue);
		players.add(player2);
		
		Game game = new Game(players);
		game.start();
		
		activeGames.add(game);
		clearOldGames();
	
		session.setAttribute(Constants.GAME_ATTRIBUTE, game);
		session.setAttribute(Constants.WEB_PLAYER_ATTRIBUTE, player2);
	}	
	
	/**
	 * Clears old games.
	 *
	 */
	private void clearOldGames() {
		logger.fine("Clearing old games");
		for(Game game : activeGames) {
			if(System.currentTimeMillis() - game.getLastActivityTime() > GAME_TIMEOUT) {
				game.interrupt();
			}
		}
	}
	
	/**
	 * Processes a player action
	 * @param player
	 * @param actionStr
	 * @param bet
	 * @param responseTime
	 */
	public void processPlayerAction(WebPlayer player, String actionStr, String bet, Long responseTime) {
		BlockingQueue<PlayerAction> queue = player.getWebplayerQueue();
		if(actionStr.equals(Constants.FOLD_LABEL)) {
			queue.add(new PlayerAction(ActionType.FOLD, 0, responseTime));
		} else if(actionStr.equals(Constants.CHECK_LABEL) || actionStr.equals(Constants.CALL_LABEL)) {
			queue.add(new PlayerAction(ActionType.CHECK_OR_CALL, 0, responseTime));
		} else if(actionStr.equals(Constants.BET_RAISE_LABEL)) {
			//If the human player tries to bet more than 1000, reset it to 1000
			if(Integer.parseInt(bet) > MAX_BET) {
				bet = Integer.toString(MAX_BET);
			}
			queue.add(new PlayerAction(ActionType.BET_OR_RAISE, Integer.parseInt(bet), responseTime));
		} else {
			throw new IllegalArgumentException("No valid player action");
		}
	}
	
	public AbstractPlayer getComputerPlayer(String filename) throws ServletException {
		System.out.println("Deserializing " + filename);
		InputStream is = this.getClass().getResourceAsStream(filename);
		InputStream buffer = new BufferedInputStream(is);
		try {
			ObjectInput input = new ObjectInputStream(buffer);
			ReinforcementLearningPlayer player = (ReinforcementLearningPlayer) input.readObject();
			player.resetBankroll();
			return player;
		} catch(ClassNotFoundException e) {
			throw new ServletException(e);
		} catch(IOException e) {
			throw new ServletException(e);
		}
	}
}