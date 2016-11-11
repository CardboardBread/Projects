
public class TrialResult {

	private int aWins;
	private int bWins;
	private int ties;
	private String winner;
	private int[] aWinsTotal;
	private int[] bWinsTotal;
	
	public TrialResult(int a, int b, int t, String w, int[] awt, int[] bwt) {
		aWins = a;
		bWins = b;
		ties = t;
		winner = w;
		aWinsTotal = awt;
		bWinsTotal = bwt;
	}

	public int getAWins () { return aWins; }
	public int getBWins () { return bWins; }
	public int getTies () { return ties; }
	public String getWinner () { return winner; }
	public int[] getAWinsTotal () { return aWinsTotal; }
	public int[] getBWinsTotal () { return bWinsTotal; }
	
	public int getAvgAWins () {
		int sum = 0;
		for (int i = 0; i < aWinsTotal.length; i++) {
			sum += aWinsTotal[i];
		}
		return sum = sum / aWinsTotal.length;
	}
	public int getAvgBWins () {
		int sum = 0;
		for (int i = 0; i < bWinsTotal.length; i++) {
			sum += bWinsTotal[i];
		}
		return sum = sum / bWinsTotal.length;
	}
}
