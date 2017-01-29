package mathematics;

import java.math.BigDecimal;

public abstract class Function extends Argument {

	protected char identifier;
	protected Variable input;
	protected Argument composed;

	public Function(char identifier, Variable input) {
		super("" + identifier, false);
		this.identifier = identifier;
		this.input = input;
	}

	public char identifier() {
		return identifier;
	}

	public Argument composed() {
		return composed;
	}

	public BigDecimal evaluate(BigDecimal input) {
		return composed.evaluate();
	}

}
