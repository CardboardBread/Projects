import java.util.Random;

public class StringTest {
	public static void main(String[] args) {
		Random random = new Random();
		StringStore str = new StringStore();
		
		for (int i = 0; i < 100; i++) {
			str.append(Math.abs(random.nextInt()));
		}
		str.remove(2);
		str.display();
	}
}
