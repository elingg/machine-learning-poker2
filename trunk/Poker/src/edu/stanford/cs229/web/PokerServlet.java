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

 public class PokerServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static Logger logger = Logger.getLogger("edu.stanford.cs229.web.PokerServlet");
	 
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
		try {
			HttpSession session = request.getSession(true);

			//Set up a new game, if there is no Game object in the seesion
			if (session.getAttribute(Constants.GAME_ATTRIBUTE) == null) {
				String name = request.getParameter(Constants.NAME_PARAMETER);
				processNewGame(name, session);
				setupLogger();
			} 
			
			WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
			
			//Process the player actions
			String actionStr = request.getParameter(Constants.ACTION_TYPE_PARAMETER);
			if(actionStr != null && !actionStr.equals("")) {
				processPlayerAction(player, actionStr);
			}
			
			//Process "play again" signal that happens at the end of a game
			String playAgain = request.getParameter(Constants.PLAY_AGAIN_PARAMETER);
			if(playAgain != null && !playAgain.equals("")) {
				player.setPlayAgainSignal(playAgain);
			}
				
			//Wait until the game is ready for the player's next action
			while (true) {
				logger.finest("Waiting");
				Thread.sleep(1000);  //this value needs to be higher than the WebPlayer threshold.
				if (player.isTurn()) {
					break;
				}
			}
			request.getRequestDispatcher("WEB-INF/game.jsp").forward(request, response);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void processNewGame(String name, HttpSession session) throws ServletException, IOException {
		//Create game if it has not been set up for this session
		
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
	
	public void processPlayerAction(WebPlayer player, String actionStr) {
		if(actionStr.equals(Constants.FOLD_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.FOLD, 0));
		}
		if(actionStr.equals(Constants.CHECK_CALL_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.CHECK_OR_CALL, 0));
		}
		if(actionStr.equals(Constants.BET_RAISE_LABEL)) {
			player.setCurrentAction(new PlayerAction(ActionType.BET_OR_RAISE, 10));
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