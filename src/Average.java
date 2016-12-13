
public class Average {

	private static int[] container;
	private static int length;

	public static void main(String[] args) {

		container = new int[100];

		for (int i = 0; i < 100; i++) {
			addData(i);
			System.out.println(i + " : " + average(container, length));
		}

	}

	public static void addData(int data) {
		if (length < container.length - 1) {
			container[length] = data;
			length++;
		} else {
			container = shiftDown(container, data);
		}
	}

	public static int[] shiftDown(int[] list, int insert) {
		for (int i = list.length - 1; i > 0; i--) {
			list[i] = list[i - 1];
		}
		list[0] = insert;
		return list;
	}

	public static int average(int[] list, int length) {
		int sum = 0;
		for (int num : list) {
			sum += num;
		}
		sum /= length;
		return sum;
	}

}
