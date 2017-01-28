package mathematics;

import java.math.BigDecimal;

public class Function {

	private char identifier;
	private Variable input;
	private Argument outputStretch;
	private Argument outputShift;
	private Argument inputStretch;
	private Argument inputShift;

	public Function(char identifier, Argument outputStretch, Argument outputShift, Argument inputStretch,
			Argument inputShift, Variable input) {
		this.identifier = identifier;
		this.inputShift = inputShift;
		this.inputStretch = inputStretch;
		this.outputShift = outputShift;
		this.outputStretch = outputStretch;
		this.input = input;
	}

	public char identifier() {
		return identifier;
	}

	public Argument inputArgument() {
		return new Parentheses(new Product(inputStretch, new Parentheses(new Difference(input, inputShift))));
	}

	public Argument asArgument() {
		return new Sum(new Product(outputStretch, inputArgument()), outputShift);
	}

	public BigDecimal evaluate(BigDecimal value) {
		Argument in = new Parentheses(
				new Product(inputStretch, new Parentheses(new Difference(new Constant(value), inputShift))));
		Argument out = new Sum(new Product(outputStretch, in), outputShift);
		return out.eval();
	}

}
