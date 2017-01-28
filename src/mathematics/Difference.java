package mathematics;

import java.math.BigDecimal;

public class Difference extends Operation {

	public static final char SYMBOL = '-';

	public Difference(Argument left, Argument right) {
		super(left, SYMBOL, right);
	}

	@Override
	protected BigDecimal eval() {
		return left.evaluate().subtract(right.evaluate());
	}

}
