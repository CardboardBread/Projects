package mathematics;

import java.math.BigDecimal;

public class Sum extends Operation {

	public static final char SYMBOL = '+';

	public Sum(Argument left, Argument right) {
		super(left, SYMBOL, right);
	}

	@Override
	protected BigDecimal eval() {
		return left.evaluate().add(right.evaluate());
	}

}
