package mathematics;

import java.util.ArrayList;

public class RadicalValue {

	public static void main(String[] args) {

		/*
		 * int radicand = 2; String number = "1."; String[] guesses = new
		 * String[10]; for (int i = 0; i < 10; i++) { String guess = number + i;
		 * guesses[i] = number + i; System.out.println(guess); }
		 * System.out.println();
		 * 
		 * String[] squares = new String[10]; for (int i = 0; i < 10; i++) {
		 * Double numGuess = Double.parseDouble(guesses[i]); squares[i] =
		 * Double.toString(Math.pow(Double.parseDouble(guesses[i]), 2));
		 * System.out.println(squares[i]); } System.out.println();
		 * 
		 * Double closest = 100d; for (int i = 0; i < 10; i++) { double distance
		 * = Math.abs(radicand - Double.parseDouble(squares[i]));
		 * System.out.println(distance); if (distance < closest) { closest =
		 * distance; } } System.out.println(closest);
		 */

		int radicand = 2;
		double radical = Math.sqrt(radicand);
		double floor = Math.floor(radical);
		String guess = Double.toString(floor).substring(0, Double.toString(floor).length() - 1);
		System.out.println("Original: " + guess);
		for (int depth = 1; depth <= 5; depth++) { // test down to 10 decimal points
			
			int closest;
			for (int n = 1; n < 10; n++) {
				System.out.println(guess + n);
				String strDecimal = guess + n;
				double decimal = Double.parseDouble(strDecimal);
				double distance = radicand - decimal;
				
			}
			guess += "3";
		}
	}

}
