package resourcecollector;

public enum HarvesterType {
	BASIC (0.5F, 1100),
	ADVANCED (0.75F, 2300),
	ELITE (0.85F, 5600);

	private final float efficiency;
	private final int storage;
	
	HarvesterType (float eff, int stor) {
		efficiency = eff;
		storage = stor;
	}
	
	public float efficiency() {return efficiency; }
	public int storage() { return storage; }
}
