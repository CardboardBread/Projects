
public class Last {

	public static void main(String[] args) {
		int[] last =  new int[] {1,2,3,4,5,6,7,8,9,0};
		System.out.print("[");
		for (int i = 0; i < last.length; i++) {
			System.out.print(last[i] + (i == last.length - 1 ? "]" : ", "));
		}
		System.out.print(": " + avg(last) + " -> ");
		last = shiftDown(last, 9);
		last = shiftDown(last, 9);
		last = shiftDown(last, 9);
		last = shiftDown(last, 9);
		System.out.print("[");
		for (int i = 0; i < last.length; i++) {
			System.out.print(last[i] + (i == last.length - 1 ? "]" : ", "));
		}
		System.out.println(": " + avg(last));
	}
	
	public static int[] shiftDown (int[] list, int insert) {
		for (int i = list.length - 1; i > 0; i--) {
			list[i] = list[i - 1];
		}
		list[0] = insert;
		return list;
	}
	
	public static int avg (int[] list) {
		int sum = 0;
		for (int i = 0; i < list.length; i++) {
			sum += list[i];
		}
		return sum / list.length;
	}

}
