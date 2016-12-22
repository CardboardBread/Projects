package mathematics;

public enum OperationType {
	ADDITION('+'),
	SUBTRACTION('-'),
	MULTIPLICATION('*'),
	DIVISION('/'),
	EXPONENTS('^');
	
	OperationType(char operator) {
		this.operator = operator;
	}
	
	private char operator;
	
	public char getChar() {
		return operator;
	}
}
