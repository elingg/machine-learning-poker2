<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.stanford.cs229.Constants" %>
<%@ page import="edu.stanford.cs229.Game" %>
<%@ page import="edu.stanford.cs229.PlayerActvityRecord" %>
<%@ page import="edu.stanford.cs229.web.WebPlayer" %>
<%
	Game game = (Game) session.getAttribute(Constants.GAME_ATTRIBUTE);
	WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);
	Boolean isEndOfGame = (Boolean) request.getAttribute(Constants.END_OF_GAME_PARAMETER);
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

<%=player.getHand().getTableCardsAsHtml() %><br/>
<br/>
<%=player.getHand().getPlayerCardsAsHtml() %><br/>
Bankroll: $<%=player.getBankroll() %> Pot: <%=player.getPot() %><br/>
<form action="PokerServlet" method="get">
	<input type="submit" name="actionType" value="<%=Constants.CHECK_CALL_LABEL %>"/>
	<input type="submit" name="actionType" value="<%=Constants.BET_RAISE_LABEL %>" />
	<input type="submit" name="actionType" value="<%=Constants.FOLD_LABEL %>" />
</form>

<%if(isEndOfGame != null && isEndOfGame) { %>
<form action="PokerServlet" method="get">
	<input type="submit" name="<%=Constants.IS_DONE_PLAYING_PARAMETER%>" value="<%=Constants.TRUE%>" />
	<input type="submit" name="<%=Constants.IS_DONE_PLAYING_PARAMETER%>" value="<%=Constants.FALSE %>" />
</form>
<%} %>

<!-- <div style="width:200px;height:300px;overflow:scroll;"> -->
<%
for(PlayerActvityRecord r : game.getGameState().getPlayerActivityRecords()) {
%><%=r.toString()%><br/><%	
}
%>

</center>


</body>
</html>