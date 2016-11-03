
public class StringStore {
	public static final String escape = "-";
	public static String data;
	
	public StringStore() {
		data = new String();
	}
	
	public String get (int index) {
		String[] split = data.split(escape);
		return split[index];
	}
	
	public boolean append (int content) {
		try {
			String[] split = data.split(escape);
			String[] newSplit = new String[split.length + 1];
			for (int i = 0; i < split.length; i++) {
				newSplit[i] = split[i];
			}
			newSplit[newSplit.length - 1] = Integer.toString(content);
			return sumArray(newSplit);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean replace (int index, int content) {
		try {
			String[] split = data.split(escape);
			split[index] = Integer.toString(content);
			return sumArray(split);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean remove (int index) {
		try {
			String[] split = data.split(escape);
			String[] newSplit = new String[split.length - 1];
			for (int i = 0; i < index; i++) {
				newSplit[i] = split[i];
			}
			for (int i = index; i < newSplit.length; i++) {
				newSplit[i] = split[i - 1];
			}
			return sumArray(newSplit);
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean sumArray (String[] subject) {
		try {
			String sum = new String();
			for (String str : subject) {
				sum += str;
				sum += escape;
			}
			data = sum;
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void display () {
		System.out.println("Original: " + data);
		String[] split = data.split(escape);
		System.out.print("Split:    [");
		for (int i = 0; i < split.length; i++) {
			System.out.print(split[i] + (i == split.length - 1 ? "]" : ","));
		}
		System.out.println("");
	}
}
