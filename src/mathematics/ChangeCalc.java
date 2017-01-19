package mathematics;

import java.util.ArrayList;

public class ChangeCalc {

	public static final int charge = 1499;

	public static void main(String[] args) {
		int remaining = charge;
		ArrayList<Coin> change = new ArrayList<Coin>();
		
		while (remaining > 0) {

			Coin largest = Coin.ONE;
			for (Coin c : Currency.Canadian.coins) {
				double delta = remaining - c.value();
				if (delta >= 0 && delta < remaining - largest.value()) {
					largest = c;
				}
			}
			change.add(largest);
			remaining -= largest.value();
		}
	}

}

enum Currency {
	Canadian(Coin.ONE, Coin.FIVE, Coin.TEN, Coin.TWENTYFIVE, Coin.HUNDRED, Coin.TWOHUNDRED),

	British(Coin.ONE, Coin.TWO, Coin.FIVE, Coin.TEN, Coin.TWENTY, Coin.FIFTY, Coin.HUNDRED, Coin.TWOHUNDRED);

	public final Coin[] coins;

	Currency(Coin... coins) {
		this.coins = coins;
	}
}

enum Coin {
	ONE(1), TWO(2), FIVE(5), TEN(10), TWENTY(20), TWENTYFIVE(25), FIFTY(50), HUNDRED(
			100), TWOHUNDRED(200);

	private final int value;

	Coin(int v) {
		value = v;
	}

	public int value() {
		return value;
	}
}
