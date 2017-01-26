package mathematics;

public class Operation extends Argument {

	private Argument left;
	private Argument right;
	private Operator type;

	public Operation(Argument left, Argument right, Operator type) {
		super(left.data() + type.symbol() + right.data(), left.isNumber() && right.isNumber());
		this.left = left;
		this.right = right;
		this.type = type;
	}

	public Argument left() {
		return left;
	}

	public Argument right() {
		return right;
	}

	public Operator type() {
		return type;
	}

	public boolean equals(Operation arg) {
		return data().equals(arg.data());
	}

}
