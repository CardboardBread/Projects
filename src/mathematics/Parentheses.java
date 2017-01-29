package mathematics;

public class Parentheses extends Container {

	public static final char OPEN_SYMBOL = '(';
	public static final char CLOSE_SYMBOL = ')';

	public Parentheses(Argument container) {
		super(OPEN_SYMBOL + container.data() + CLOSE_SYMBOL, container.isNumber());
		this.container = container;
	}

}
