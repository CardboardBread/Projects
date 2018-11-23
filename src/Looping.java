
public class Looping {

	public static void main(String[] args) {

		// for loop made as a while loop
		int increment = 0;
		int upper = 10;
		while (increment < upper) {
			System.out.println(increment);
			increment++;
		}
		
		// while loop made as a for loop
		boolean conditional = false;
		for (int i = 0; conditional == true; i++) {
			System.out.println(i);
		}
		
		// unorthodox for loop use
		int eye = 0;
		for (eye = 0; eye < 10; eye++) {
			System.out.println(eye);
		}
	}

}
