package mathematics;

public class Ratio extends Argument {

	private double numerator;
	private double denominator;

	public Ratio(double a, double b) {
		super(true);
		numerator = a;
		denominator = b;
	}

	public double evaluate() {
		return numerator / denominator;
	}

	public double numerator() {
		return numerator;
	}

	public double denominator() {
		return denominator;
	}
	
	public String toString() {
		return "" + numerator + "/" + denominator;
	}

}
