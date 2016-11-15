
public class StringTest {
	public static void main(String[] args) {
		StringStore2 str = new StringStore2();
		
		str.append("hey");
		str.append("goon");
		str.append("giant");
		str.append("trouble");
		str.display();
		str.remove(0);
		str.display();
	}
}
