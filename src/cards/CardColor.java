package cards;

public enum CardColor {
	BLACK, RED;

	public static CardColor fromOrdinal(int ordinal) {
		if (ordinal < 0 || ordinal > values().length - 1)
			return null;

		return values()[ordinal];
	}

	@Override
	public String toString() {
		return name();
	}
}
