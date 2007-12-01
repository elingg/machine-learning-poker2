<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="edu.stanford.cs229.AbstractPlayer" %>
<%@ page import="edu.stanford.cs229.Card" %>
<%@ page import="edu.stanford.cs229.Constants" %>
<%@ page import="edu.stanford.cs229.Game" %>
<%@ page import="edu.stanford.cs229.PlayerActivityRecord" %>
<%@ page import="edu.stanford.cs229.web.WebPlayer" %>
<%
	//P3P headers to allow this to work in IE7
	response.setHeader("P3P","CP='NOI DSP NID TAIo PSAa OUR IND UNI OTC TST'");
	Game game = (Game) session.getAttribute(Constants.GAME_ATTRIBUTE);
	WebPlayer player = (WebPlayer) session.getAttribute(Constants.WEB_PLAYER);

	AbstractPlayer opponent = (AbstractPlayer) game.getGameState().getOpponent(player);
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

<!-- COMPUTER PLAYER (NAME AND POT) -->
<b><%=opponent.getName()%></b><br/>
<b>Bankroll</b>: $<%=opponent.getBankroll()%> | <b>Pot</b>: <%=opponent.getPot()%><br/>

<!-- COMPUTER PLAYER CARDS (EITHER HIDDEN OR SHOWN) -->
<%if(!game.getGameState().isShowCards()) {%>
  <%=Card.getHiddenCardsAsHtml()%><br/>
<%} else {%>
  <%=opponent.getHand().getPlayerCardsAsHtml()%>
<%}%>

<!-- TABLE CARDS -->
<br/>
<%=player.getHand().getTableCardsAsHtml()%><br/>
<br/>

<!-- HUMAN PLAYER (NAME AND POT) -->

<%=player.getHand().getPlayerCardsAsHtml()%><br/>
<br/>
<b><%=player.getName()%></b><br/>
<b>Bankroll</b>: $<%=player.getBankroll()%> | <b>Pot</b>: <%=player.getPot()%><br/>


<!-- CHECK/BET/FOLD BUTTONS -->
<%if(!game.getGameState().isEndOfGame()) {%>
<form action="PokerServlet" method="get">
<%if(game.getGameState().arePotsEqual()) {%>
	<input type="submit" name="actionType" value="<%=Constants.CHECK_LABEL %>"/>
<% } else {%>
	<input type="submit" name="actionType" value="<%=Constants.CALL_LABEL %>"/>
<% } %>
	<input type="submit" name="actionType" value="<%=Constants.BET_RAISE_LABEL %>" />
	<input type="submit" name="actionType" value="<%=Constants.FOLD_LABEL %>" />
	<br/>
	Bet/Raise Amount: <input type="text" size="2" name="<%=Constants.BET_PARAMETER %>" value="10" />
</form>
<%} else {%>
<form action="PokerServlet" method="get">
	<input type="submit" name="<%=Constants.PLAY_AGAIN_PARAMETER%>" value="Play Again" />
</form>
<%}%>

<!-- ACTIVITY FOR THIS GAME -->
<%
	for(PlayerActivityRecord r : game.getGameState().getPlayerActivityRecords()) {
	String bgColor = "#FFCF73";
	if(r.getPhaseNum() % 2 == 0) {
		bgColor = "#FFA600";
	}
%>
<div style="width:400px;background-color:<%=bgColor%>"><%=r.toString()%></div>
<%}%>

</center>

<!-- GOOGLE ANALYTICS CODE -->
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-3089908-1";
urchinTracker();
</script>

</body>
</html>