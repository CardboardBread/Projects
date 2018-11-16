package math;

import java.util.ArrayList;
import java.util.List;

public class Number {

	public static final char decimalPoint = '.';

	private List<Integer> wholeDigits;
	private List<Integer> fracDigits;

	public Number(String number) {
		String[] parts = number.split(".");
		wholeDigits = new ArrayList<Integer>();
		fracDigits = new ArrayList<Integer>();

		if (parts.length > 2) {
			throw new IllegalArgumentException("Input string contains more than one decimal point!");
		}
		
		if (parts.length > 1) {

		} else {
			try {
				int test = Integer.parseInt(number);
				
				for (int i = 0; i < number.length(); i++) {
					
				}
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Input string was not a proper number!", nfe);
			}
		}

		
	}

}
