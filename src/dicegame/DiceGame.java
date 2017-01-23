package dicegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class DiceGame {
	public static void main(String[] args) {
		ArrayList<int[]> results = new ArrayList<int[]>();
		for (int i = 0; i < 20; i++) {
			results.add(trial());
		}

		double aSum = 0;
		System.out.print("[");
		for (int i = 0; i < results.size(); i++) {
			aSum += results.get(i)[0];
			System.out.print(results.get(i)[0] + (i == results.size() - 1 ? "]" : ", "));
		}
		System.out.println(" " + aSum / results.size());

		double bSum = 0;
		System.out.print("[");
		for (int i = 0; i < results.size(); i++) {
			bSum += results.get(i)[1];
			System.out.print(results.get(i)[1] + (i == results.size() - 1 ? "]" : ", "));
		}
		System.out.println(" " + bSum / results.size());

		double tSum = 0;
		System.out.print("[");
		for (int i = 0; i < results.size(); i++) {
			tSum += results.get(i)[2];
			System.out.print(results.get(i)[2] + (i == results.size() - 1 ? "]" : ", "));
		}
		System.out.println(" " + tSum / results.size());
	}

	private static int[] trial() {
		int aWins = 0;
		int bWins = 0;
		int ties = 0;

		ArrayList<Dice> playerA = new ArrayList<Dice>(
				Arrays.asList(Dice.A, Dice.A, Dice.B, Dice.B, Dice.C, Dice.C, Dice.D, Dice.D));
		ArrayList<Dice> playerB = new ArrayList<Dice>(
				Arrays.asList(Dice.A, Dice.A, Dice.B, Dice.B, Dice.C, Dice.C, Dice.D, Dice.D));

		Collections.shuffle(playerB);

		Iterator<Dice> IteratorB = playerB.iterator();

		while (IteratorB.hasNext()) {
			Dice bDice = IteratorB.next();
			int bRoll = rollDie(bDice);
			Dice aDice = counter(bDice, playerA);
			int aRoll = rollDie(aDice);
			// System.out.println("B:" + bDice.name() + "," + bRoll + " A:" +
			// aDice.name() + "," + aRoll);

			if (aRoll > bRoll) {
				aWins++;
			} else if (aRoll == bRoll) {
				bWins++;
			} else {
				ties++;
			}

			IteratorB.remove();
		}

		return new int[] { aWins, bWins, ties };
	}

	private static Dice counter(Dice counter, ArrayList<Dice> list) {
		ArrayList<Dice> pool = list;

		switch (counter) {
		case A:
			if (pool.contains(Dice.B)) {
				return pool.remove(pool.indexOf(Dice.B));
			} else if (pool.contains(Dice.A)) {
				return pool.remove(pool.indexOf(Dice.A));
			} else if (pool.contains(Dice.C)) {
				return pool.remove(pool.indexOf(Dice.C));
			} else if (pool.contains(Dice.D)) {
				return pool.remove(pool.indexOf(Dice.D));
			}
			break;
		case B:
			if (pool.contains(Dice.C)) {
				return pool.remove(pool.indexOf(Dice.C));
			} else if (pool.contains(Dice.B)) {
				return pool.remove(pool.indexOf(Dice.B));
			} else if (pool.contains(Dice.D)) {
				return pool.remove(pool.indexOf(Dice.D));
			} else if (pool.contains(Dice.A)) {
				return pool.remove(pool.indexOf(Dice.A));
			}
			break;
		case C:
			if (pool.contains(Dice.A)) {
				return pool.remove(pool.indexOf(Dice.A));
			} else if (pool.contains(Dice.D)) {
				return pool.remove(pool.indexOf(Dice.D));
			} else if (pool.contains(Dice.C)) {
				return pool.remove(pool.indexOf(Dice.C));
			} else if (pool.contains(Dice.B)) {
				return pool.remove(pool.indexOf(Dice.B));
			}
			break;
		case D:
			if (pool.contains(Dice.A)) {
				return pool.remove(pool.indexOf(Dice.A));
			} else if (pool.contains(Dice.B)) {
				return pool.remove(pool.indexOf(Dice.B));
			} else if (pool.contains(Dice.D)) {
				return pool.remove(pool.indexOf(Dice.D));
			} else if (pool.contains(Dice.C)) {
				return pool.remove(pool.indexOf(Dice.C));
			}
			break;
		default:
			return null;
		}
		return counter;
	}

	private static int rollDie(Dice die) {
		Random rand = new Random();
		return die.side(rand.nextInt(die.count));
	}

}

enum Dice {
	A(7, 7, 7, 7, 1, 1),

	B(5, 5, 5, 5, 5, 5),

	C(9, 9, 3, 3, 3, 3),

	D(8, 8, 8, 2, 2, 2);

	private final int[] sides;
	public final int count = 6;

	Dice(int... sides) {
		this.sides = sides;
	}

	public int side(int index) {
		return sides[index];
	}
}