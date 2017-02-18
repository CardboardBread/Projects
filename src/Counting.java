import java.util.ArrayList;

public class Counting {

	public static void main(String[] args) {
		
		ArrayList<Numerals> numbers = new ArrayList<Numerals>();
		int number = 811985549;
		String sNumber = Integer.toString(number);
		int significant = sNumber.length() / 3;
		
		//System.out.println(number);
		//System.out.println(significant);
		
		for (int i = 0; i < significant; i++) {
			int start = 3 * i;
			int end = 3 * (i + 1);
			String piece = sNumber.substring(start, end);
			System.out.println(start);
			System.out.println(end);
			System.out.println(piece);
		}
		
		
		
	}

}

enum Numerals {
	ONE(1),
	TWO(1),
	THREE(1),
	FOUR(1),
	FIVE(1),
	SIX(1),
	SEVEN(2),
	EIGHT(1),
	NINE(1),
	TEN(1),
	ELEVEN(3),
	TWELVE(1),
	THIRTEEN(2),
	FOURTEEN(2),
	FIFTEEN(2),
	SIXTEEN(2),
	SEVENTEEN(2),
	EIGHTEEN(2),
	NINETEEN(2),
	TWENTY(2),
	THIRTY(2),
	FOURTY(2),
	FIFTY(2),
	SIXTY(2),
	SEVENTY(3),
	EIGHTY(2),
	NINETY(2),
	HUNDRED(2),
	THOUSAND(2),
	MILLION(3),
	BILLION(3);
	
	private final int syllables;
	
	Numerals(int syllables) {
		this.syllables = syllables;
	}
	
	public int syllables() {
		return syllables;
	}
	
	
}
