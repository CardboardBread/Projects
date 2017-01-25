

public class Decimal {

	private String value;
	private int decimalIndex;
	private boolean decimal;

	public <E extends Number> Decimal(E value) {
		this.value = value.toString();
	}
	
	public Decimal(Decimal in) {
		this.value = in.value;
		this.decimalIndex = in.decimalIndex;
		this.decimal = in.decimal;
	}

	private int decimalIndex() {

		return decimalIndex = value.indexOf('.');
	}

	public String getRaw() {
		return value;
	}

	private long value() {
		String whole = "";

		if (decimalIndex < 0) {
			whole = value;
		} else {
			whole = value.substring(0, decimalIndex);
			whole += value.substring(decimalIndex + 1, value.length());
		}
		
		try {
			return Long.parseLong(whole);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return 0L;
	}

	public static void main(String[] args) {
		Decimal d = new Decimal(Double.MAX_VALUE);
		System.out.println(d.getRaw());
		System.out.println(d.decimalIndex());
		System.out.println(d.value());
	}

}
