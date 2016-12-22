package mathematics;

public class Operation extends Argument {

	private double A;
	private OperationType type;
	private double B;

	public Operation(double a, OperationType op, double b) {
		super(true);
		A = a;
		B = b;
		type = op;
	}
	
	public double evaluate() {
		switch (type) {
		case ADDITION:
			return A + B;
		case SUBTRACTION:
			return A - B;
		case MULTIPLICATION:
			return A * B;
		case DIVISION:
			return A / B;
		case EXPONENTS:
			return Math.pow(A, B);
		default:
			return Double.NaN;
		}
	}

}
