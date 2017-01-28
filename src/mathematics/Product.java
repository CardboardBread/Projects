package mathematics;

import java.math.BigDecimal;

public class Product extends Operation {

	public static final char SYMBOL = '*';

	public Product(Argument left, Argument right) {
		super(left, SYMBOL, right);
	}

	@Override
	protected BigDecimal eval() {
		return left.evaluate().multiply(right.evaluate());
	}

}
