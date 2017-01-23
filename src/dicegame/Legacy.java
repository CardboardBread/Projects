package dicegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Legacy {

	public static final int SAMPLE_SIZE = 1000;
	public static final int TRIALS = 10000;
	public static final int ROUNDS = 8;

	public static final int[] DiceA = { 7, 7, 7, 7, 1, 1 };
	public static final int[] DiceB = { 5, 5, 5, 5, 5, 5 };
	public static final int[] DiceC = { 9, 9, 3, 3, 3, 3 };
	public static final int[] DiceD = { 8, 8, 8, 2, 2, 2 };

	public static ArrayList<TrialResult> results;

	public static int rollDie(String die) {
		Random rand = new Random();
		if (die.contains("A")) {
			return DiceA[rand.nextInt(DiceA.length)];
		} else if (die.contains("B")) {
			return DiceB[rand.nextInt(DiceB.length)];
		} else if (die.contains("C")) {
			return DiceC[rand.nextInt(DiceC.length)];
		} else if (die.contains("D")) {
			return DiceD[rand.nextInt(DiceD.length)];
		} else {
			return 0;
		}
	}

	public static TrialResult trial(int trialCount, int rounds) {
		int aWins = 0;
		int bWins = 0;
		int ties = 0;
		int[] aWinsTotal = new int[trialCount];
		int[] bWinsTotal = new int[trialCount];
		String[][] aStrat = new String[trialCount][rounds];
		String[][] bStrat = new String[trialCount][rounds];

		for (int i = 0; i < trialCount; i++) {
			// System.out.println("Trial " + (i + 1));
			int aWinsTrial = 0;
			int bWinsTrial = 0;

			ArrayList<String> playerA = new ArrayList<String>(Arrays.asList("A", "A", "B", "B", "C", "C", "D", "D"));
			ArrayList<String> playerB = new ArrayList<String>(Arrays.asList("A", "A", "B", "B", "C", "C", "D", "D"));
			Collections.shuffle(playerA);
			Collections.shuffle(playerB);

			for (int k = 0; k < rounds; k++) {
				int aRoll = rollDie(playerA.get(k));
				int bRoll = rollDie(playerB.get(k));
				aStrat[i][k] = playerA.get(k);
				bStrat[i][k] = playerB.get(k);
				// System.out.println("A rolled " + aRoll + " and B rolled " +
				// bRoll);
				if (aRoll > bRoll) {
					// System.out.println("A wins!");
					aWinsTrial++;
				} else if (bRoll > aRoll) {
					// System.out.println("B wins!");
					bWinsTrial++;
				} else {
					// System.out.println("Tie!");
				}
			}

			// System.out.println("A won " + aWinsTrial + " times");
			// System.out.println("B won " + bWinsTrial + " times");
			aWinsTotal[i] = aWinsTrial;
			bWinsTotal[i] = bWinsTrial;
			if (aWinsTrial > bWinsTrial) {
				aWins++;
			} else if (bWinsTrial > aWinsTrial) {
				bWins++;
			} else {
				ties++;
			}
		}
		// System.out.println("A won " + aWins + " trials");
		// System.out.println("B won " + bWins + " trials");
		// System.out.println("They tied in " + ties + " trials");
		String winner;
		if (aWins > bWins) {
			// System.out.println("A won over " + trialCount + " " + rounds +
			// "-round trials with a " + (aWins - bWins) + " trial lead");
			winner = "A";
		} else if (bWins > aWins) {
			// System.out.println("B won over " + trialCount + " " + rounds +
			// "-round trials with a " + (bWins - aWins) + " trial lead");
			winner = "B";
		} else {
			// System.out.println("Both players tied over " + trialCount + " " +
			// rounds + "-round trials");
			winner = "T";
		}
		return new TrialResult(aWins, bWins, ties, winner, aWinsTotal, bWinsTotal, aStrat, bStrat);
	}

	public static void sampleCollector(ArrayList<TrialResult> subject) {
		int aWins = 0;
		int bWins = 0;
		int ties = 0;
		for (TrialResult result : subject) {
			if (result.getWinner() == "A") {
				aWins++;
			} else if (result.getWinner() == "B") {
				bWins++;
			} else if (result.getWinner() == "T") {
				ties++;
			}
		}
		System.out.println("A won " + aWins + " samples, B won " + bWins + " samples, tying " + ties + " times");
	}

	public static void main(String[] args) {
		results = new ArrayList<TrialResult>();
		for (int i = 0; i < SAMPLE_SIZE; i++) {
			results.add(trial(TRIALS, ROUNDS));
		}
		sampleCollector(results);
	}

}

class TrialResult {

	private int aWins;
	private int bWins;
	private int ties;
	private String winner;
	private int[] aWinsTotal;
	private int[] bWinsTotal;
	private String[][] aStrategy;
	private String[][] bStrategy;

	public TrialResult(int a, int b, int t, String w, int[] awt, int[] bwt, String[][] ast, String[][] bst) {
		aWins = a;
		bWins = b;
		ties = t;
		winner = w;
		aWinsTotal = awt;
		bWinsTotal = bwt;
		aStrategy = ast;
		bStrategy = bst;
	}

	public int getAWins() {
		return aWins;
	}

	public int getBWins() {
		return bWins;
	}

	public int getTies() {
		return ties;
	}

	public String getWinner() {
		return winner;
	}

	public int[] getAWinsTotal() {
		return aWinsTotal;
	}

	public int[] getBWinsTotal() {
		return bWinsTotal;
	}

	public int getTrialSize() {
		return aWinsTotal.length;
	}

	public String[] getAStrat(int trial) {
		return aStrategy[trial];
	}

	public String[] getBStrat(int trial) {
		return bStrategy[trial];
	}

	public int getAvgAWins() {
		int sum = 0;
		for (int i = 0; i < aWinsTotal.length; i++) {
			sum += aWinsTotal[i];
		}
		return sum / aWinsTotal.length;
	}

	public int getAvgBWins() {
		int sum = 0;
		for (int i = 0; i < bWinsTotal.length; i++) {
			sum += bWinsTotal[i];
		}
		return sum / bWinsTotal.length;
	}
}
