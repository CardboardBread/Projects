package mathematics;

public class Test {

	public static void main(String[] args) {
		Ratio a = new Ratio(134,934);
		System.out.println(a.toString());
		Ratio b = RatioUtil.simplify(a);
		System.out.println(b.toString());
		
		Exponent e = new Exponent(2,5);
		System.out.println(e.evaluate());
		
		Operation op = new Operation(11,OperationType.DIVISION,2);
		System.out.println(op.evaluate());
	}

}
