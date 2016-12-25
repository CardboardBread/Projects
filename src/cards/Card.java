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

	public boolean equals(CardType type) {
		if (this.type == type) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(CardSuite suite) {
		if (this.suite.equals(suite)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals(CardColor color) {
		if (this.color.equals(color)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == this.getClass()) {
			if (((Card) obj).type.equals(this.type()) && ((Card) obj).suite.equals(this.suite())
					&& ((Card) obj).color.equals(this.color())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
