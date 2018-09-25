package cards;

public enum CardColor {
	BLACK, RED;

	public static CardColor fromOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal > values().length - 1)
			return null;

		return values()[ordinal];
	}
	
	public CardColor getOpposite() {
		if (this == CardColor.BLACK) {
			return CardColor.RED;
		} else {
			return CardColor.BLACK;
		}
	}

	@Override
	public String toString() {
		return name();
	}
}
