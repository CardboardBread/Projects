package mathematics;

public class Ratio extends Argument {

	public static final char SYMBOL = '/';

	private Argument numerator;
	private Argument denominator;

	public Ratio(Argument numerator, Argument denominator) {
		super(numerator.data() + SYMBOL + denominator.data(), numerator.isNumber() && denominator.isNumber());
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public Argument numerator() {
		return numerator;
	}

	public Argument denominator() {
		return denominator;
	}

	public boolean equals(Ratio arg) {
		return data().equals(arg.data());
	}

}
