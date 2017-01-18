package cards;

import java.util.Stack;

public class Deck {
	public static final int DECK_SIZE = 52;

	private Stack<Card> stack;

	public Deck() {
		stack = new Stack<Card>();

		for (int suite = 0; suite < 4; suite++) {
			for (int card = 0; card < 13; card++) {
				stack.push(new Card(CardType.fromOrdinal(card), CardSuite.fromOrdinal(suite)));
			}
		}
	}

	public Deck(Card[] cards) {
		stack = new Stack<Card>();

		for (Card card : cards) {
			stack.push(card);
		}
	}

	public Card draw() {
		return stack.pop();
	}

	public Card check() {
		return stack.peek();
	}

	public Card add(Card card) {
		if (stack.size() < DECK_SIZE) {
			stack.push(card);
			return card;
		} else {
			return null;
		}
	}

}
