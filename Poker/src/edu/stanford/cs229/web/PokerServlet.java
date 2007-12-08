package edu.stanford.cs229.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
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
import edu.stanford.cs229.LogFormatter;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.RandomPlayer;
import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

 public class PokerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	public static String LOG_DIR = "/tmp/poker/"; 
	//public static String LOG_DIR = "c:/";
	
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.web.PokerServlet");
	private static boolean loggersSetup = false;
	
	/**
	 * Constructor
	 */
	public PokerServlet() {
		super();
		setupLogger();
	}   	
	
	/**
	 * GET Web interface
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	/**
	 * Post Web interface
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
				Object isFacebook = request.getAttribute(Constants.IS_FACEBOOK);
				if(isFacebook == null) {
					//Non-Facebook
					System.out.println("Non Facebook");
					String name = request.getParameter(Constants.NAME_PARAMETER);
					String id = request.getRemoteAddr();
					processNewGame(id, name, session);
				} else {
					System.out.println("Facebook");
					
					//Facebook
					String name = (String) request.getAttribute(Constants.NAME_PARAMETER);
					String id = ((Integer) request.getAttribute(Constants.ID_PARAMETER)).toString();
					
					System.out.println("Name: " + name);
					System.out.println("ID: " + id);
					
					processNewGame(id, name, session);
				}
			} 
			
			WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
			
			//Process the player actions
			String actionStr = request.getParameter(Constants.ACTION_TYPE_PARAMETER);
			String bet = request.getParameter(Constants.BET_PARAMETER);
			
			//If the human player tries to bet more than 1000, reset it to 1000
			if(Integer.parseInt(bet) > 1000) {
				bet = "1000";
			}
			
			if(actionStr != null && !actionStr.equals("")) {
				Long responseTime = System.currentTimeMillis() - (Long) session.getAttribute(Constants.TIMESTAMP);
				processPlayerAction(player, actionStr, bet, responseTime);
			}
			
			//Process "play again" signal that happens at the end of a game
			String playAgain = request.getParameter(Constants.PLAY_AGAIN_PARAMETER);
			if(playAgain != null && !playAgain.equals("")) {
				player.setPlayAgainSignal(playAgain);
			}
				
			//Wait until the game is ready for the player's next action
			while (true) {
				logger.finest("Waiting");
				Thread.sleep(1000);  //this value needs to be 2x higher than the WebPlayer threshold.
				if (player.isTurn()) {
					break;
				}
			}
			session.setAttribute(Constants.TIMESTAMP, new Long(System.currentTimeMillis()));
			request.getRequestDispatcher("WEB-INF/game.jsp").forward(request, response);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up a new game
	 * @param id
	 * @param name
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void processNewGame(String id, String name, HttpSession session) throws ServletException, IOException {
		//Create game if it has not been set up for this session
		
		if(name == null || name.equals("")) {
			name = "Guest";
		}
		
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();

		//RandomPlayer player1 = new RandomPlayer(Constants.WEBAPP_OPPONENT_NAME);
		AbstractPlayer player1 = getComputerPlayer();
		player1.setId("Computer-" + id);
		
		WebPlayer player2 = new WebPlayer(name, id);
		players.add(player1);
		players.add(player2);
		Game game = new Game(players);
		game.start();
		session.setAttribute(Constants.GAME_ATTRIBUTE, game);
		session.setAttribute(Constants.WEB_PLAYER, player2);
	}
	
	/**
	 * Processes a player action
	 * @param player
	 * @param actionStr
	 * @param bet
	 * @param responseTime
	 */
	public void processPlayerAction(WebPlayer player, String actionStr, String bet, Long responseTime) {
		if(actionStr.equals(Constants.FOLD_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.FOLD, 0, responseTime));
		} else if(actionStr.equals(Constants.CHECK_LABEL) || actionStr.equals(Constants.CALL_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.CHECK_OR_CALL, 0, responseTime));
		} else if(actionStr.equals(Constants.BET_RAISE_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.BET_OR_RAISE, Integer.parseInt(bet), responseTime));
		} else {
			throw new IllegalArgumentException("No valid player action");
		}
	}
	
	public AbstractPlayer getComputerPlayer() throws ServletException {
		String SAVED_PLAYER = "Computer";
		InputStream is = this.getClass().getResourceAsStream(SAVED_PLAYER);
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
	
	/**
	 * Hack to get loggers set up.
	 * TODO: Find out a better way to do logging
	 */
	private void setupLogger() {
		if (!loggersSetup) {
			// Create a console handler
			ConsoleHandler handler = new ConsoleHandler();
			handler.setLevel(Level.FINEST);
			
			// Add to logger
			Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
			logger.addHandler(handler);
			logger.setLevel(Level.FINEST);
			
			
			//Setup file handler logger
	        // Create an appending file handler
			try {
				boolean append = true;
				FileHandler fileHandler = new FileHandler(LOG_DIR + "PlayerActivityRecord.log", append);
				fileHandler.setFormatter(new LogFormatter());
				fileHandler.setLevel(Level.FINEST);
				//Add to the desired logger
				Logger fileLogger = Logger.getLogger("file.PlayerActivityRecord");
				fileLogger.addHandler(fileHandler);
				fileLogger.setLevel(Level.FINEST);
			} catch(IOException e) {
				throw new RuntimeException("Could not set up logger");
			}
		}
		loggersSetup = true;
	}
}