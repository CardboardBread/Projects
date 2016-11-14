package resourcecollector;

public class ResourceTile extends MapTile {

	private ResourceType type;
	private int maxCapacity;
	private int capacity;
	private int lastCap;
	private boolean state;
	
	public ResourceTile(ResourceType type, int cap) {
		this.type = type;
		maxCapacity = cap;
		capacity = new Integer(maxCapacity);
	}
	
	public void cycle() {
		int increase = (int) (capacity * (1 + type.restoration()));
		if (capacity * increase > maxCapacity) {
			capacity = new Integer(maxCapacity);
		} else {
			capacity = capacity * increase;
		}
	}
	
	public int harvest (float efficiency) {
		int decrease = (int) (efficiency * type.yield() * type.rate());
		if (decrease >= capacity) {
			int yield = new Integer(capacity);
			capacity = 0;
			return yield;
		} else {
			capacity -= decrease;
			return decrease;
		}
	}
	
}
