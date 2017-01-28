package mathematics;

import java.math.BigDecimal;

public class Exponent extends Argument {

	public static final char SYMBOL = '^';

	private Argument base;
	private Argument exponent;

	public Exponent(Argument base, Argument exponent) {
		super(base.data() + SYMBOL + exponent.data(), base.isNumber() && exponent.isNumber());
		this.base = base;
		this.exponent = exponent;
	}

	public Argument base() {
		return base;
	}

	public Argument exponent() {
		return exponent;
	}
	
	@Override
	protected BigDecimal eval() {
		return base.evaluate().pow(exponent.evaluate().intValue());
		
	}

}
