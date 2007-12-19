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

	/**
	 * Deployment steps:
	 * 1. Change log dir here
	 * 2. Make sure max games in Game.java is high
	 */
	public static String LOG_DIR = "/tmp/poker/"; 
	//public static String LOG_DIR = "c:/";
	
	private static String DEFAULT_BOT_CLASS = "ReinforcementLearningPlayer";
	private static String DEFAULT_BOT_PACKAGE = "edu.stanford.cs229.ml";
	
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
				
				//Get the Java Class of the bot
				String botClassName = request.getParameter(Constants.BOT_PARAMETER);
				if(botClassName == null || botClassName.equals("")) {
					botClassName = DEFAULT_BOT_CLASS;  //default is MLPlayer
				}
				
				//Get whether to deserialize the bot from disk.  By default, deserialize
				boolean deserializeBot = true;
				String deserializeBotParameter = request.getParameter(Constants.DESERIALIZE_BOT_PARAMETER);
				if(deserializeBotParameter != null && deserializeBotParameter.equals("0")) {
					deserializeBot = false;
				}
				
				Object isFacebook = request.getAttribute(Constants.IS_FACEBOOK);
				if(isFacebook == null) {
					//Non-Facebook
					System.out.println("Non Facebook");
					String name = request.getParameter(Constants.NAME_PARAMETER);
					String id = name + request.getRemoteAddr() + botClassName;
					processNewGame(id, name, botClassName, deserializeBot, session);
				} else {
					System.out.println("Facebook");
					
					//Facebook
					String name = (String) request.getAttribute(Constants.NAME_PARAMETER);
					String id = ((Integer) request.getAttribute(Constants.ID_PARAMETER)).toString() + botClassName;
					
					System.out.println("Name: " + name);
					System.out.println("ID: " + id);
					
					processNewGame(id, name, botClassName, deserializeBot, session);
				}
			} 
			
			WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
			
			//Process the player actions
			String actionStr = request.getParameter(Constants.ACTION_TYPE_PARAMETER);
			String bet = request.getParameter(Constants.BET_PARAMETER);
			
			//If the human player tries to bet more than 1000, reset it to 1000
			try {
				if(Integer.parseInt(bet) > 1000) {
					bet = "1000";
				}
			} catch(NumberFormatException e) {
				
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
		
		WebPlayer player2 = new WebPlayer(playerName, playerId);
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