package gcode;

public class GParameter {
	
	private Character header;
	private double value;
	
	public GParameter(char header) {
		this.header = header;
	}

	public GParameter(char header, double value) {
		this.header = header;
		this.value = value;
	}
	
	public char getHeader() {
		return header;
	}
	
	public void setHeader(char header) {
		this.header = header;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return header.toString() + value;
	}

}
