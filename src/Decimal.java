public class Decimal {

	public static final char decimalCharacter = '.';

	public static void getRelative(Decimal first, Decimal last) {

		while (first.getWholeLength() > last.getWholeLength()) {
			last.addLeadingZero();
		}

		while (first.getFractionLength() > last.getFractionLength()) {
			last.addTrailingZero();
		}

		while (last.getWholeLength() > first.getWholeLength()) {
			first.addLeadingZero();
		}

		while (last.getFractionLength() > first.getFractionLength()) {
			first.addTrailingZero();
		}
	}

	public static Decimal sum(Decimal first, Decimal last) {
		Decimal.getRelative(first, last);
		String firstRaw = first.getRaw();
		String lastRaw = last.getRaw();
		String result = "";
		boolean carry = false;
		int index = first.getIndex();

		for (int i = firstRaw.length() - 1; i > -1; i--) {
			int sum = Integer.parseInt(firstRaw.substring(i, i + 1)) + Integer.parseInt(lastRaw.substring(i, i + 1));

			if (carry) {
				sum += 1;
				carry = false;
			}

			if (sum > 9) {
				sum -= 10;
				carry = true;
			}

			result += sum;

		}

		if (carry) {
			result += "1";
			index++;
		}

		String out = "";

		for (int i = result.length() - 1; i > -1; i--) {
			out += result.charAt(i);
		}

		return new Decimal(out, index);
	}

	public static Decimal difference(Decimal first, Decimal last) {
		Decimal.getRelative(first, last);

		String firstRaw = first.getRaw();
		String lastRaw = last.getRaw();
		String result = "";
		boolean carry = false;

		// for (int i = 0; )
		return null;
	}

	private String rawNumber;
	private int decimalIndex;

	public Decimal(String rawNumber) {
		decimalIndex = rawNumber.indexOf('.');
		if (decimalIndex == -1) {
			this.rawNumber = rawNumber;
			decimalIndex = rawNumber.length();
			addTrailingZero();
		} else {
			this.rawNumber = rawNumber.substring(0, decimalIndex)
					+ rawNumber.substring(decimalIndex + 1, rawNumber.length());
		}
	}

	public Decimal(String rawNumber, int decimalIndex) {
		this.rawNumber = rawNumber;
		this.decimalIndex = decimalIndex;
		while (this.decimalIndex > this.rawNumber.length() - 1) {
			addTrailingZero();
		}
	}

	public Decimal(Decimal copy) {
		this.rawNumber = copy.rawNumber;
		this.decimalIndex = copy.decimalIndex;
	}

	public int getWholeLength() {
		return 0 + decimalIndex;
	}

	public int getFractionLength() {
		return rawNumber.length() - decimalIndex;
	}

	public int getLength() {
		return rawNumber.length() + 1;
	}

	public String getRaw() {
		return rawNumber;
	}

	public int getIndex() {
		return decimalIndex;
	}

	public void addLeadingZero() {
		rawNumber = "0" + rawNumber;
		decimalIndex += 1;
	}

	public void addTrailingZero() {
		rawNumber += "0";
	}

	public String getFormat() {
		String frontHalf = rawNumber.substring(0, decimalIndex);
		String backHalf = rawNumber.substring(decimalIndex, rawNumber.length());
		return frontHalf + decimalCharacter + backHalf;
	}

	public void getRelative(Decimal relative) {

		while (relative.getWholeLength() > getWholeLength()) {
			addLeadingZero();
		}

		while (relative.getFractionLength() > getFractionLength()) {
			addTrailingZero();
		}

	}

}
