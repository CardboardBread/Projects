import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stores any data in a single string which also contains the length of each element in the string.
 * Storing the length of each element is pointless.
 * Contains a non-assembled version of the string, to improve parsing each element of the string.
 * @author mike_
 *
 */
public class StringStore2 {
	public static String splitChar = ",";
	public static String lengthChar = "-";
	public static String fullData;
	public static String[][] splitData;

	public StringStore2() {
		
	}
	
	private void update() {
		splitData = deconstruct(fullData);
	}
	
	public String append (String input) {
		
		int length = input.length();
		String out = input + lengthChar + length;
		if (fullData == null) {
			fullData = out;
		} else {
			fullData += splitChar;
			fullData += out;
		}
		update();
		return out;
	}
	
	public String get (int index) {
		update();
		return lengthHalfConstruct(splitData[index]);
	}
	
	public String remove (int index) {
		update();
		String grab = get(index);
		String[][] newSplit = new String[splitData.length - 1][2];
		List<String[]> list = new ArrayList<String[]>(Arrays.asList(splitData));
		list.remove(index);
		newSplit = list.toArray(newSplit);
		fullData = construct(newSplit);
		update();
		return grab;
	}
	
	private String[][] deconstruct (String data) {
		String[] elements = data.split(splitChar);
		String[][] full = new String[elements.length][2];
		int index = 0;
		for (String str : elements) {
			String[] split = str.split(lengthChar);
			full[index][0] = split[0];
			full[index][1] = split[1];
			index++;
		}
		return full;
	}
	
	private String[] splitHalfDeconstruct (String data) {
		return data.split(splitChar);
	}
	
	private String[][] lengthHalfDeconstruct (String[] data) {
		String[][] out = new String[data.length][2];
		for (int i = 0; i < data.length; i++) {
			String[] split = data[i].split(splitChar);
			out[i] = split;
		}
		return out;
	}
	
	public String construct (String[][] data) {
		String out = "";
		for (int i = 0; i < data.length; i++) {
			out += data[i][0];
			out += lengthChar;
			out += data[i][1];
			out += (i == data.length - 1 ? "" : splitChar);
		}
		return out;
	}
	
	private String[] splitHalfConstruct (String[][] data) {
		String[] out = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			out[i] = data[i][0] + lengthChar + data[i][1];
		}
		return out;
	}
	
	private String lengthHalfConstruct (String[] data) {
		String out = "";
		for (int i = 0; i < data.length; i++) {
			out += data[i];
			out += (i == data.length - 1 ? splitChar : "");
		}
		return out;
	}
	
	public void display() {
		update();
		System.out.print("[");
		for (int i = 0; i < splitData.length; i++) {
			System.out.print(splitData[i][0] + lengthChar + splitData[i][1] + (i == splitData.length - 1 ? "]" : splitChar));
		}
		System.out.println();
	}

}
