package mathematics;

import java.math.BigDecimal;

public class Argument {

	private String data;
	private boolean isNumber;

	public Argument(String data) {
		this.data = data;
		try {
			Double.parseDouble(data);
		} catch (NumberFormatException nfe) {
			isNumber = false;
		}
		isNumber = true;
	}

	public Argument(String data, boolean number) {
		this.data = data;
		isNumber = number;
	}

	public boolean isNumber() {
		return isNumber;
	}

	public String data() {
		return data;
	}

	public BigDecimal evaluate() {
		if (isNumber()) {
			return eval();
		} else {
			return null;
		}
	}

	protected BigDecimal eval() {
		return new BigDecimal(data);
	}

	public boolean equals(Argument arg) {
		return data.equals(arg.data()) && isNumber == arg.isNumber();
	}

}
