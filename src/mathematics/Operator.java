package mathematics;

public enum Operator {
	ADD('+'), SUBTRACT('-'), MULTIPLY('*'), DIVIDE('/'), EQUALS('=');

	private char symbol;

	private Operator(char symbol) {
		this.symbol = symbol;
	}

	public char symbol() {
		return symbol;
	}

}
