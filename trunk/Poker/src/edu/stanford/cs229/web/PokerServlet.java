package edu.stanford.cs229.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ActionType;
import edu.stanford.cs229.Constants;
import edu.stanford.cs229.Game;
import edu.stanford.cs229.PlayerAction;
import edu.stanford.cs229.RandomPlayer;
import edu.stanford.cs229.ml.ReinforcementLearningPlayer;

 public class PokerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	public PokerServlet() {
		super();
	}   	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}  	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute(Constants.GAME_ATTRIBUTE);
		
		if (obj == null) {
			//Set up a new game
			processNewGame(request, response, session);
			setupLogger();
		} else {
			//Continue the session
			Game game = (Game) obj;
			boolean isEndOfGame = game.getGameState().isEndOfGame();
			WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
			
			//Unblock the game if it is waiting for a decision to be made
			String isDonePlaying = request.getParameter(Constants.IS_DONE_PLAYING_PARAMETER);
			if(isDonePlaying != null && !isDonePlaying.equals("")) {
				player.setDecisionSignal(isDonePlaying);
			}
			
			if(!isEndOfGame) {
				processPlayerAction(request, response, session);
			} else {
				request.setAttribute(Constants.END_OF_GAME_PARAMETER, new Boolean(true));
				//Show page with "Continue: Yes/No" button and show results
			}
			
			
			
		}
		
		WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
		// Check if thread is awake again
		try {
			while (true) {
				Thread.sleep(1000);  //this value needs to be higher than the WebPlayer threshold.
				if (player.isTurn()) {
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		request.getRequestDispatcher("WEB-INF/game.jsp").forward(request, response);
	}
	
	public void processNewGame(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		//Create game if it has not been set up for this session
		String name = request.getParameter(Constants.NAME_PARAMETER);
		if(name == null || name.equals("")) {
			name = "Guest";
		}
		List<AbstractPlayer> players = new ArrayList<AbstractPlayer>();

		RandomPlayer player1 = new RandomPlayer(Constants.WEBAPP_OPPONENT_NAME);
		WebPlayer player2 = new WebPlayer(name);
		players.add(player1);
		players.add(player2);
		Game game = new Game(players);
		game.start();
		session.setAttribute(Constants.GAME_ATTRIBUTE, game);
		session.setAttribute(Constants.WEB_PLAYER, player2);
	}
	
	public void processPlayerAction(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
		String actionStr = request.getParameter(Constants.ACTION_TYPE_PARAMETER);
		WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
		if(actionStr.equals(Constants.FOLD_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.FOLD, 0));
		}
		if(actionStr.equals(Constants.CHECK_CALL_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.CHECK_OR_CALL, 0));
		}
		if(actionStr.equals(Constants.BET_RAISE_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.BET_OR_RAISE, 0));
		}

	}
	
	private void setupLogger() {
	    // Create a console handler
	    ConsoleHandler handler = new ConsoleHandler();
	    handler.setLevel(Level.FINE);
	    // Add to logger
	    Logger logger = Logger.getLogger("edu.stanford.cs229.Game");
	    logger.addHandler(handler);
	    logger.setLevel(Level.FINE);
	}
}