package cards;

import java.util.ArrayList;

public class Hand {

	private ArrayList<Card> hand;

	public Hand() {
		hand = new ArrayList<Card>();
	}

	public Hand(Card[] stack) {
		hand = new ArrayList<Card>();
		for (Card c : stack) {
			hand.add(c);
		}
	}

	public Card pick(int index) {
		return hand.remove(index);
	}

	public Card get(int index) {
		return hand.get(index);
	}

	public void add(Card subject) {
		hand.add(subject);
	}
	
	public Card[] list() {
		return hand.toArray(new Card[hand.size()]);
	}

}
