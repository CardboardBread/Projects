package mathematics;

public class Operation extends Argument {

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

}
