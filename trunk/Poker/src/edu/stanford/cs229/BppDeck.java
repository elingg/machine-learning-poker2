package edu.stanford.cs229;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class BppDeck extends Deck {

	public Card drawCard() {
		while (true) {
			try {
				InputStreamReader isr = new InputStreamReader(System.in);
				BufferedReader stdin = new BufferedReader(isr);
				System.out.print("Enter card value (2 to 14):");
				Integer value = Integer.parseInt(stdin.readLine());

				System.out.print("Enter card suit (1 Hearts, 2 Diamonds, 3 Clubs, 4 Spades):");
				Integer suit = Integer.parseInt(stdin.readLine());

				return new Card(value, suit);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}