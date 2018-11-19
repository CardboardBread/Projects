package gcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCommand {

	private GParameter header;
	private List<GParameter> parameters;

	public GCommand() {
	}
	
	public GCommand(GParameter... parameters) {
		List<GParameter> backHalf = Arrays.asList(parameters).subList(1, parameters.length);
		this.header = parameters[0];
		this.parameters = new ArrayList<GParameter>(backHalf);
	}

	public GParameter getCommand() {
		return header;
	}

	public void setCommand(GParameter command) {
		header = command;
	}

	public GParameter[] getParameters() {
		return parameters.toArray(new GParameter[3]);
	}

	public void setParameters(List<GParameter> parameters) {
		this.parameters = parameters;
	}

	public void setParameters(GParameter... parameters) {
		this.parameters = new ArrayList<GParameter>(Arrays.asList(parameters));
	}
	
	@Override
	public String toString() {
		String out = header.toString();
		for (GParameter gparam : parameters) {
			System.out.println(gparam);
			out += ", " + gparam.toString();
		}
		return out;
	}

}
