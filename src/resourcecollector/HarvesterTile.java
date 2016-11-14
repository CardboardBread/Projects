package resourcecollector;

public class HarvesterTile extends MapTile {

	private HarvesterType tier;
	private int maxStorage;
	private int storage;
	
	public HarvesterTile(HarvesterType tier, int max) {
		this.tier = tier;
		maxStorage = max;
		storage = 0;
	}
	
	public void harvest (ResourceTile target) {
		int yield = target.harvest(tier.efficiency());
		if (yield + storage > maxStorage) {
			storage = new Integer(maxStorage);
		} else {
			storage += yield;
		}
	}

}
