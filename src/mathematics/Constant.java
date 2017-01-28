package mathematics;

import java.math.BigDecimal;

public class Constant extends Argument {

	private BigDecimal value;
	private boolean isFraction = true;

	public <E extends Number> Constant(Number value) {
		super(value.toString(), true);
		if (value.doubleValue() == Math.round(value.doubleValue()))
			isFraction = false;
		if (isFraction) {
			value = new BigDecimal(value.doubleValue());
		} else {
			value = new BigDecimal(value.longValue());
		}
	}

	public BigDecimal value() {
		return value;
	}

	public boolean isFraction() {
		return isFraction;
	}

}
