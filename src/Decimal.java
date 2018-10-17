public class Decimal {
	
	public static final char decimalCharacter = '.';
	
	public static void relative(Decimal first, Decimal second) {
		if (first.getWholeLength() > second.getWholeLength()) {
			int lengthDiff = first.getWholeLength() - second.getWholeLength();
			
			for (int i = 0; i < lengthDiff; i++) {
				second.addLeadingZero();
			}
		} else if (first.getWholeLength() < second.getWholeLength()) {
			int lengthDiff = second.getWholeLength() - first.getWholeLength();
			
			for (int i = 0; i < lengthDiff; i++) {
				first.addLeadingZero();
			}
		}
		
		if (first.getFractionLength() > second.getFractionLength()) {
			int lengthDiff = first.getFractionLength() - second.getFractionLength();
			
			for (int i = 0; i < lengthDiff; i++) {
				second.addTrailingZero();
			}
		} else if (first.getFractionLength() < second.getFractionLength()) {
			int lengthDiff = second.getFractionLength() - first.getFractionLength();
			
			for (int i = 0; i < lengthDiff; i++) {
				first.addTrailingZero();
			}
		}
	}
	
	public static Decimal sum(Decimal first, Decimal second) {
		Decimal.relative(first, second);
		
		String firstRaw = first.getRaw();
		String secondRaw = second.getRaw();
		int length = first.getRaw().length();
		
		int carry = 0;
		for (int i = length - 1; i > -1; i--) {
			System.out.println(i);
			carry = add(firstRaw.charAt(i), secondRaw.charAt(i));
		}
		
		return null;
	}
	
	private static char add(char first, char second) {
		return 0;
	}
	
	public static void main(String[] args) {
		Decimal dec = new Decimal("1234", 1);
		Decimal dek = new Decimal("1234", 3);
		System.out.println(dec.getFormat());
		System.out.println(dec.getWholeLength());
		System.out.println(dec.getFractionLength());
		System.out.println();
		System.out.println(dek.getFormat());
		System.out.println(dek.getWholeLength());
		System.out.println(dek.getFractionLength());
		Decimal.relative(dec, dek);
		System.out.println(dec.getFormat());
		System.out.println(dek.getFormat());
		System.out.println(dec.getRaw());
		System.out.println(dek.getRaw());
		Decimal.sum(dec, dek);
	}

	private String rawNumber;
	private int decimalIndex;
	
	public Decimal(String rawNumber) {
		// scan for a decimal point, if there is none, place one at the end
	}
	
	public Decimal(String rawNumber, int decimalIndex) {
		this.rawNumber = rawNumber;
		this.decimalIndex = decimalIndex;
		while (this.decimalIndex > this.rawNumber.length() - 1) {
			addTrailingZero();
		}
	}
	
	public int getWholeLength() {
		return 0 + decimalIndex;
	}
	
	public int getFractionLength() {
		return rawNumber.length() - decimalIndex;
	}
	
	public String getRaw() {
		return rawNumber;
	}
	
	public void addLeadingZero() {
		rawNumber = "0" + rawNumber;
		decimalIndex += 1;
	}
	
	public void addTrailingZero() {
		rawNumber = rawNumber + "0";
	}
	
	public String getFormat() {
		String frontHalf = rawNumber.substring(0, decimalIndex);
		String backHalf = rawNumber.substring(decimalIndex, rawNumber.length());
		return frontHalf + decimalCharacter + backHalf;
	}
	
	public void cleanZeroes() {
		
	}

}
