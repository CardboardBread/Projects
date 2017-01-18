package mathematics;

public class Exponent extends Argument {

	private double base;
	private double power;

	public Exponent(double b, double x) {
		super(true);
		base = b;
		power = x;
	}

	public double evaluate() {
		return Math.pow(base, power);
	}

	public double power() {
		return power;
	}

	public double base() {
		return base;
	}

}
