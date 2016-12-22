package mathematics;

public class ExponentUtil {
	
	public static Exponent multiply (Exponent a, Exponent b) {
		if (a.base() == b.base()) {
			return new Exponent(a.base(),a.power() + b.power());
		} else {
			return null;
		}
	}
	
	public static Exponent divide (Exponent a, Exponent b) {
		if (a.base() == b.base()) {
			return new Exponent(a.base(), a.power() - b.power());
		} else {
			return null;
		}
	}

}
