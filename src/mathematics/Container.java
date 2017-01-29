package mathematics;

import java.math.BigDecimal;

public abstract class Container extends Argument {

	protected Argument container;

	public Container(String data, boolean isNumber) {
		super(data, isNumber);
	}

	public Argument container() {
		return container;
	}

	@Override
	protected BigDecimal eval() {
		return container.evaluate();
	}

}
