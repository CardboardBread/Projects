package mathematics;

import java.math.BigDecimal;

public class Variable extends Argument {

	private char identifier;
	private BigDecimal value;

	public Variable(char identifier) {
		super("" + identifier, false);
		this.identifier = identifier;
	}

	public char identifier() {
		return identifier;
	}

	public BigDecimal value(BigDecimal value) {
		this.value = value;
		return value;
	}

	public BigDecimal value() {
		return value;
	}

}
