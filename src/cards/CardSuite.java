package cards;

public enum CardSuite {
	SPADES(CardColor.BLACK), CLUBS(CardColor.BLACK), DIAMONDS(CardColor.RED), HEARTS(CardColor.RED);

	private final CardColor color;

	private CardSuite(CardColor color) {
		this.color = color;
	}

	public static CardSuite fromOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal > values().length - 1)
			return null;

		return values()[ordinal];
	}

	public CardSuite getPrevious() {
		int newOrdinal = ordinal() - 1;

		if (newOrdinal < 0) {
			return fromOrdinal(CardSuite.values().length - 1);
		} else {
			return fromOrdinal(newOrdinal);
		}
	}

	public CardSuite getNext() {
		int newOrdinal = ordinal() + 1;

		if (newOrdinal > values().length - 1) {
			return fromOrdinal(0);
		} else {
			return fromOrdinal(newOrdinal);
		}
	}

	public CardColor color() {
		return color;
	}

	@Override
	public String toString() {
		return name();
	}
}
