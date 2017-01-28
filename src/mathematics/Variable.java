package mathematics;

public class Variable extends Argument {

	protected char identifier;
	public Number value;

	public Variable(char identifier) {
		super("" + identifier, false);
		this.identifier = identifier;
	}

	public char identifier() {
		return identifier;
	}

	public <E extends Number> Number value(E value) {
		this.value = value;
		return value;
	}

	public Number value() {
		return value;
	}

}
