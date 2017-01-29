package mathematics;

import java.math.BigDecimal;

public abstract class Operation extends Argument {

	protected Argument left;
	protected Argument right;

	public Operation(Argument left, char operation, Argument right) {
		super(left.data() + operation + right.data(), left.isNumber() && right.isNumber());
		this.left = left;
		this.right = right;
	}

	public Argument left() {
		return left;
	}

	public Argument right() {
		return right;
	}

	protected abstract BigDecimal eval();

}
