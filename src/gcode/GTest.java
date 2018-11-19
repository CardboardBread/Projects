package gcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GTest {

	public static void main(String[] args) {
		ArrayList<String> data = new ArrayList<String>();
		String file = GTest.class.getResource("test_gcode.txt").getPath();

		try {
			data.addAll(FileLoader.readFile(file));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String s : data) {
			System.out.println(s + " : ");
			//System.out.println(interpret(s));
			//System.out.println();
		}
	}

	public static GCommand interpret(String subject) {
		List<GParameter> parts = new ArrayList<GParameter>();
		
		int paramFlag = 0;
		GParameter working = null;
		String arg = "";
		for (int i = 0; i < subject.length(); i++) {
			char index = subject.charAt(i);
			
			if (index == ' ') {
				if (paramFlag > 1) {
					working.setValue(Double.parseDouble(arg));
					parts.add(working);
					working = null;
				}
				paramFlag = 0;
				continue;
			}
			
			else if (Character.isLetter(index)) {
				working = new GParameter(index);
				paramFlag++;
			}
			
			else if (Character.isDigit(index) && paramFlag > 0) {
				paramFlag++;
				arg += index;
			}
			
			else if (paramFlag > 0) {
				paramFlag++;
				arg += index;
			}
			
		}
		
		return new GCommand(parts.toArray(new GParameter[4]));
	}

}
