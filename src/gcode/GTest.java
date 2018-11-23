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
		
		int commandFlag = 0;
		GParameter working = null;
		String valueStr = "";
		for (int i = 0; i < subject.length(); i++) {
			char index = subject.charAt(i);
			
			if (Character.isLetter(index)) {
				if (commandFlag < 1) {
					commandFlag++;
					working = new GParameter(index);
					valueStr = "";
				}
				if (commandFlag > 0) {
					commandFlag++;
					parts.add(working);
					working = null;
					working = new GParameter(index);
					valueStr = "";
				}
				
				continue;
			}
			
			if (Character.isDigit(index)) {
				if (commandFlag > 0) {
					valueStr += index;
				}
				continue;
			}
			
		}
		
		return new GCommand(parts.toArray(new GParameter[4]));
	}

}
