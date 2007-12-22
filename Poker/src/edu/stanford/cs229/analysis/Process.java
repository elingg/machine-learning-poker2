package edu.stanford.cs229.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.stanford.cs229.Card;
import edu.stanford.cs229.Deck;
import edu.stanford.cs229.Hand;
import edu.stanford.cs229.Util;

public class Process {
	private static Logger logger = Logger
			.getLogger("edu.stanford.cs229.analysis");

	public static void main(String[] s) {

		//Hand hand = createHand("T:[3;3][4;4][9;2] P:[5;1][2;2]");
		//0.706003
		/*
		 Hand hand = createHand("T:[14;4][6;1][12;3][6;3][4;2] P:[7;1][5;3]");
		 logger.info("Player Cards: " + hand.getPlayerCards().toString());
		 logger.info("Table Cards: " + hand.getTableCards().toString());
		 logger.info("All Cards: " + hand.getAllCards().toString());
		 if(hand.getAllCards().size() >= 2)
		 System.out.println("Outs are: " + computeOuts(hand));
		 */
		try {

			/*	Sets up a file reader to read the file passed on the command
			 line one character at a time */
			FileReader input = new FileReader("hands.txt");
			FileWriter fstream = new FileWriter("hands.out.txt");
			BufferedWriter out = new BufferedWriter(fstream);

			/* Filter FileReader through a Buffered read to read a line at a
			 time */
			BufferedReader bufRead = new BufferedReader(input);

			String line; // String that holds current file line
			int count = 0; // Line number of count 

			// Read first line
			line = bufRead.readLine();
			count++;

			// Read through file one line at time. Print line # and line
			while (line != null) {
				//System.out.println(count+": "+line);
				Hand hand = createHand(line);
				if (hand.getAllCards().size() >= 3) {
					//System.out.println(hand.toString());
					String output = count + " Outs are: "
							+ (1 - computeOuts(hand));
					System.out.println(output);
					out.write(output + "\n");
					out.flush();
				} else {
					System.out.println(count);
					out.write(count + "\n");
				}
				line = bufRead.readLine();
				count++;
			}

			bufRead.close();
			out.close();
		} catch (ArrayIndexOutOfBoundsException e) {
			/* If no file was passed on the command line, this expception is
			 generated. A message indicating how to the class should be
			 called is displayed */
			System.out.println("Usage: java ReadFile filename\n");

		} catch (IOException e) {
			// If another exception is generated, print a stack trace
			e.printStackTrace();
		}
	}

	public static Hand createHand(String s) {
		Hand hand = new Hand();

		String[] cards = s.split(" ");

		//Table cards
		String[] tableCards = cards[0].split("]");
		for (String tableCard : tableCards) {
			Card c = getCard(tableCard);
			if (c != null)
				hand.addTableCard(c);

		}

		String[] playerCards = cards[1].split("]");
		for (String playerCard : playerCards) {
			Card c = getCard(playerCard);
			if (c != null)
				hand.addPlayerCard(getCard(playerCard));
		}
		return hand;
	}

	public static Card getCard(String s) {
		s = s.replace("T", "");
		s = s.replace("P", "");
		s = s.replace(":", "");
		s = s.replace("[", "");
		if (!s.equals("")) {
			String[] parts = s.split(";");
			int value = Integer.parseInt(parts[0]);
			int suit = Integer.parseInt(parts[1]);
			return new Card(value, suit);
		} else {
			return null;
		}
	}

	public static float computeOuts(Hand hand) {
		Deck tempDeck1 = new Deck();
		Deck tempDeck2 = new Deck();
		Deck tempDeck3 = new Deck();
		Deck tempDeck4 = new Deck();
		Card card1;
		Card card2;
		Card card3;
		Card card4;

		int numLosses = 0;
		int numRounds = 0;
		List<Card> tableCards = hand.getTableCards();
		List<Card> myCards = hand.getPlayerCards();

		Hand myHand = new Hand();
		Hand opponentsHand = new Hand();

		for (int i = 1; i <= 52; i++) {
			//System.out.println("i: " + i);
			card1 = tempDeck1.drawCard();
			if (tableCards.contains(card1) || myCards.contains(card1))
				continue;
			tempDeck2.shuffleDeck();
			for (int j = 1; j <= 52; j++) {
				//System.out.println("j: " + j);
				card2 = tempDeck2.drawCard();
				if (tableCards.contains(card2) || myCards.contains(card2)
						|| card1.equals(card2))
					continue;

				tempDeck3.shuffleDeck();

				if (hand.getTableCards().size() == 4) {

					for (int k = 1; k <= 52; k++) {
						//System.out.println("k: " + k);
						card3 = tempDeck3.drawCard();
						if (tableCards.contains(card3)
								|| myCards.contains(card3)
								|| card1.equals(card3) || card2.equals(card3))
							continue;

						myHand = new Hand();
						opponentsHand = new Hand();

						ArrayList<Card> test1 = new ArrayList<Card>();
						test1.add(card3);
						test1.addAll(hand.getTableCards());
						test1.addAll(hand.getPlayerCards());
						myHand.setAllCards(test1);

						ArrayList<Card> test = new ArrayList<Card>();
						test.add(card1);
						test.add(card2);
						test.add(card3);
						test.addAll(hand.getTableCards());
						opponentsHand.setAllCards(test);

						Hand winningHand = Util.findWinner(myHand,
								opponentsHand);
						if (winningHand != null
								&& winningHand.equals(opponentsHand))
							numLosses++;
						numRounds++;

					}
				} else {
					myHand = new Hand();
					opponentsHand = new Hand();

					myHand.setAllCards(hand.getAllCards());

					ArrayList<Card> test = new ArrayList<Card>();
					test.add(card1);
					test.add(card2);
					test.addAll(hand.getTableCards());
					opponentsHand.setAllCards(test);

					Hand winningHand = Util.findWinner(myHand, opponentsHand);
					if (winningHand != null
							&& winningHand.equals(opponentsHand))
						numLosses++;
					numRounds++;

				}

			}
		}

		return (float) numLosses / (float) numRounds;
	}
}
