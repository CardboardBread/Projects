package cards;

public class Card {

	private CardType type;
	private CardSuite suite;
	private CardColor color;

	public Card(CardType type, CardSuite suite) {
		this.type = type;
		this.suite = suite;
		color = suite.color();

	}

	public CardType type() {
		return type;
	}

	public CardSuite suite() {
		return suite;
	}

	public CardColor color() {
		return color;
	}

	public String toString() {
		return super.toString() + " " + type.toString() + " of " + suite.toString();
	}

	public boolean equals(Card card) {
		return (this.type() == card.type()) && (this.suite() == card.suite());
	}

}
