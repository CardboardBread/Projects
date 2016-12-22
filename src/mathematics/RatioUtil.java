package mathematics;

public class RatioUtil {

	public static Ratio simplify (Ratio subject) {
		
		double max = 0;
		double divisor = 0;
		
		if (subject.numerator() > subject.denominator()) {
			max = subject.numerator();
		} else {
			max = subject.denominator();
		}
		
		for (double i = 1; i <= max; i++) {
			if (subject.numerator() % i == 0 && subject.denominator() % i == 0) {
				divisor = i;
			}
		}
		
		if (divisor != 0) {
			return new Ratio(subject.numerator() / divisor, subject.denominator() / divisor);
		} else {
			return null;
		}
	}
}
