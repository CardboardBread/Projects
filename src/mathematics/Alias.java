package mathematics;

public class Alias extends Container {

	private char identifier;

	public Alias(char identifier, Argument container) {
		super("" + identifier, container.isNumber());
		this.identifier = identifier;
	}

	public char idenifier() {
		return identifier;
	}
}
