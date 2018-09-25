package cards;

public enum CardType {
	ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,

	JACK, QUEEN, KING;

	public static CardType fromOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal > values().length - 1)
			return null;

		return values()[ordinal];
	}

	public CardType getPrevious() {
		int newOrdinal = ordinal() - 1;

		if (newOrdinal < 0) {
			return fromOrdinal(CardType.values().length - 1);
		} else {
			return fromOrdinal(newOrdinal);
		}
	}

	public CardType getNext() {
		int newOrdinal = ordinal() + 1;

		if (newOrdinal > values().length - 1) {
			return fromOrdinal(0);
		} else {
			return fromOrdinal(newOrdinal);
		}
	}

	public boolean isNumerical() {
		if (this.ordinal() < 10) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFaced() {
		if (this.ordinal() > 9) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return name();
	}

}
