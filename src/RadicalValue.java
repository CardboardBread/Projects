

import java.util.ArrayList;

public class RadicalValue {

	public static void main(String[] args) {

		String square = "2";
		int iSquare = 2;
		String radicand = "1.";
		String solution = radicand;

		for (int k = 0; k < 40; k++) {
			String[] radicands = new String[10];
			for (int i = 0; i < 10; i++) {
				radicands[i] = solution + i;
			}

			double[] squares = new double[10];
			for (int i = 0; i < 10; i++) {
				//System.out.print(radicands[i] + " ");
				double test = Double.parseDouble(radicands[i]);
				squares[i] = Math.pow(test, 2);
			}
			//System.out.println();

			double[] deltas = new double[10];
			double lowest = 100d;
			int lowPos = 0;
			for (int i = 0; i < 10; i++) {
				deltas[i] = Math.abs(squares[i] - iSquare);
				if (deltas[i] < lowest) {
					lowest = deltas[i];
					lowPos = i;
				}
				//System.out.print(deltas[i] + " ");
			}
			String out = radicands[lowPos];
			solution += out.substring(out.length() - 1);
		}
		System.out.println(solution);
	}
}
