<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.stanford.cs229.AbstractPlayer" %>
<%@ page import="edu.stanford.cs229.Card" %>
<%@ page import="edu.stanford.cs229.Constants" %>
<%@ page import="edu.stanford.cs229.Game" %>
<%@ page import="edu.stanford.cs229.PlayerActvityRecord" %>
<%@ page import="edu.stanford.cs229.web.WebPlayer" %>
<%
	Game game = (Game) session.getAttribute(Constants.GAME_ATTRIBUTE);
	WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
	boolean isEndOfGame = game.getGameState().isEndOfGame();
	AbstractPlayer opponent = (AbstractPlayer) game.getGameState().getOpponent();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Poker Game</title>
<link rel="stylesheet" href="style.css" type="text/css"/>
</head>
<body>
<center>
<br/>

<!-- COMPUTER PLAYER -->
<b><%=opponent.getName()%></b><br/>
<b>Bankroll</b>: $<%=opponent.getBankroll() %> | <b>Pot</b>: <%=opponent.getPot() %><br/>


<%if(!isEndOfGame) { %>
  <%=Card.getHiddenCardsAsHtml() %><br/>
<%} else {%>
  <%=opponent.getHand().getPlayerCardsAsHtml() %>
<%}%>


<!-- TABLE CARDS -->
<br/>
<%=player.getHand().getTableCardsAsHtml() %><br/>
<br/>

<!-- HUMAN PLAYER -->

<%=player.getHand().getPlayerCardsAsHtml() %><br/>
<br/>
<b><%=player.getName()%></b><br/>
<b>Bankroll</b>: $<%=player.getBankroll() %> | <b>Pot</b>: <%=player.getPot() %><br/>


<%if(!isEndOfGame) { %>
<form action="PokerServlet" method="get">
	<input type="submit" name="actionType" value="<%=Constants.CHECK_CALL_LABEL %>"/>
	<input type="submit" name="actionType" value="<%=Constants.BET_RAISE_LABEL %>" />
	<input type="submit" name="actionType" value="<%=Constants.FOLD_LABEL %>" />
</form>
<%} else {%>
<form action="PokerServlet" method="get">
	<input type="submit" name="<%=Constants.PLAY_AGAIN_PARAMETER%>" value="Play Again" />
</form>
<%}%>


<%
for(PlayerActvityRecord r : game.getGameState().getPlayerActivityRecords()) {
	String bgColor = "#FFCF73";
	if(r.getPhaseNum() % 2 == 0) {
		bgColor = "#FFA600";
	}
%>
<div style="width:400px;background-color:<%=bgColor%>"><%=r.toString()%></div>
<%}%>

</center>
</body>
</html>