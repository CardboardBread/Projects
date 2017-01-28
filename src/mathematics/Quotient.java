package mathematics;

import java.math.BigDecimal;

public class Quotient extends Operation {

	public static final char SYMBOL = '/';

	public Quotient(Argument left, Argument right) {
		super(left, SYMBOL, right);
	}

	@Override
	protected BigDecimal eval() {
		return left.evaluate().divide(right.evaluate());
	}

}
