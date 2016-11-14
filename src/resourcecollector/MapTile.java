package resourcecollector;

import java.util.Random;

public class MapTile {
	
	private long tileID;

	public MapTile() {
		Random rand = new Random();
		tileID = Math.abs(rand.nextLong());
	}
	
	public long getID() { return tileID; }

}
