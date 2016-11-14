package resourcecollector;

public enum ResourceType {
	Blue (1, 1.875F, 0.15F),
	Green (1, 1, 0.475F);
	
	private final float rate; // quantity of harvest operations that can be done in a cycle
	private final float yield; // # of units from each harvest
	private final float restoration; // rate of growth to maximum capacity
	
	ResourceType(float r, float y, float rest) {
		rate = r;
		yield = y;
		restoration = rest;
	}
	
	public float rate() { return rate; }
	public float yield() { return yield; }
	public float restoration() { return restoration; }
}
