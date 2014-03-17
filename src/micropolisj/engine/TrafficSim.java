package micropolisj.engine;

/**
 * 
 * @author schwtony
 * Simluates traffic stuff
 */

public class TrafficSim {
	Micropolis engine;
	
	public TrafficSim(){
		this(new Micropolis());
	}
	
	public TrafficSim(Micropolis city){
		engine = city;
	}
	
	public void findEnd(int posx, int posy){
		// determines the end of a way starting at a given field
		
	}
	
	
	public int findWay(int startx, int starty, int endx, int endy){
		// finds way from A to B, if exists
		// in general, does A*-algorithm		
		return 0;
	}
	
	public int[] changePeriphereRoad(int posx, int posy){
		// finds out if there are streets next to our field
		
		int[] object= new int[4];
		object[0]=object[1]=object[2]=object[3]=0;
		return object;
	}
	
	public void makeTraffic(int x, int y){
		// increases the traffic on a given tile, depending on what type it is of
	}
	
	
}
