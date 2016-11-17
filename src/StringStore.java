/**
 * Stores integers in a single String, splitting and reforming it when edits are made.
 * Uses a signature escape character to properly split the string into a usable array.
 * Since the string needs to be initialized empty, the empty character is seen as an initial entry into the string.
 * @author mike_
 * Finished November 3rd, 2016
 */
public class StringStore {
	public static String escape = "-";
	public static String data;
	
	/**
	 * constructor method, initializing the storage variable and setting the escape string.
	 */
	public StringStore() {
		data = new String();
	}
	
	/**
	 * Returns the data stored at the following index.
	 * @param index The location of the desired data.
	 * @return
	 */
	public String get (int index) {
		String[] split = data.split(escape);
		return split[index];
	}
	
	/**
	 * Adds the input onto the end of the string.
	 * @param content The data to be added.
	 * @return
	 */
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
	
	/**
	 * Replaces the following index with the provided data.
	 * @param index The location of the data to replace.
	 * @param content The data that will take the place of the indexed data.
	 * @return
	 */
	public boolean replace (int index, int content) {
		try {
			String[] split = data.split(escape);
			split[index] = Integer.toString(content);
			return sumArray(split);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Removes the provided data from the string, without removing the space the data was contained in.
	 * This results in empty spaces.
	 * @param index
	 * @return
	 */
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
