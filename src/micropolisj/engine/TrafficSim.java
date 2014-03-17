package micropolisj.engine;

import java.math.*;
import java.util.HashMap;

/**
 * 
 * @author schwtony
 * Simluates traffic stuff
 */
public class TrafficSim {
	Micropolis engine;
	HashMap<Integer,SpecifiedTile> unready;
	HashMap<CityLocation,SpecifiedTile> ready;
	
	
	
	
	public TrafficSim(){
		this(new Micropolis());
	}
	
	public TrafficSim(Micropolis city){
		engine = city;
	}
	
	public void findEnd(CityLocation startpos){
		// determines the end of a way starting at a given field
		
	}
	
	
	public int findWay(CityLocation startpos, CityLocation endpos){
		// finds way from A to B, if exists
		// in general, does A*-algorithm
		return 0;
	}
	
	
	public int[] changePeriphereRoad(CityLocation pos){
		// finds out if there are streets next to our field
		// and codes them to an integer list (4 entrys: north, east, south, west)
		
		int[] object= new int[4];
		object[0]=object[1]=object[2]=object[3]=0;
		return object;
	}
	
	public void makeTraffic(CityLocation pos, int value){
		// increases the traffic on a given tile by value
	}
	
	public static int evalfunc(CityLocation start, CityLocation finish){
		return Math.abs(start.x-finish.x)+Math.abs(start.y-finish.y)+1;
	}
	
	
	
	
	
}
