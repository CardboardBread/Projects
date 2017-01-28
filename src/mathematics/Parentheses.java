package mathematics;

import java.math.BigDecimal;

public class Parentheses extends Argument {

	public static final char OPEN_SYMBOL = '(';
	public static final char CLOSE_SYMBOL = ')';

	private Argument container;

	public Parentheses(Argument container) {
		super(OPEN_SYMBOL + container.data() + CLOSE_SYMBOL, container.isNumber());
		this.container = container;
	}

	public Argument container() {
		return container;
	}

	@Override
	protected BigDecimal eval() {
		return container.evaluate();
	}
}
