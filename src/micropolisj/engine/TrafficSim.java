package micropolisj.engine;

import java.math.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * Simluates traffic stuff
 */
public class TrafficSim {
	Micropolis engine;
	HashMap<Integer,SpecifiedTile> unready;
	HashMap<CityLocation,SpecifiedTile> ready;
	HashMap<CityLocation,Integer> mapBack;
	HashSet<CityLocation> goal;
	HashSet<CityLocation> found;
	
	
	
	
	public TrafficSim(){
		this(new Micropolis());
	}
	
	public TrafficSim(Micropolis city){
		engine = city;
		ready = new HashMap<CityLocation,SpecifiedTile>();
		unready = new HashMap<Integer,SpecifiedTile>();
		goal = new HashSet<CityLocation>();
		found = new HashSet<CityLocation>();
		mapBack = new HashMap<CityLocation,Integer>();
	}
	/**
	 * The function is called to generate traffic from the starting position
	 * uses A*-Algorithm to find ways
	 * @param startP starting position
	 * @return length of the way (-1 for no way)
	 */
	public int genTraffic(CityLocation startP) {
		CityLocation end=findEnd(startP);
		int way=findWay(startP,end);
		if (way!=-1) {
			engine.putVisits(startP);
			engine.putVisits(end);
		}
		return way;
	}
	
	/**
	 * determinates the end of a way starting at a given field
	 * @param startpos
	 * @return the endpos
	 */
	
	private CityLocation findEnd(CityLocation startpos){
		return startpos;
	}
	
	/**
	 * finds way from A to B, if exists
	 * in general, does A*-algorithm
	 * and increase traffic along the way
	 * @param startpos
	 * @param endpos
	 * @return length of the way
	 */
	
	public int findWay(CityLocation startpos, CityLocation endpos){
		int currentCost=0;
		CityLocation currentLocation=new CityLocation(-1,-1);
		found.clear();
		ready=findPeriphereRoad(startpos);
		goal=(HashSet<CityLocation>) findPeriphereRoad(endpos).keySet();
		found=(HashSet<CityLocation>) ready.keySet();
		int best=200;
		if (ready.isEmpty()) {
			return -1;
		}
		for (CityLocation f : ready.keySet()) {
			for (CityLocation g : findAdjRoads(f)) {
				int keyi=16384*evalfunc(f,goal)+g.y;
				unready.put(keyi,new SpecifiedTile(g,f,false));
				mapBack.put(g, keyi);
			}
		}
		while (!unready.isEmpty() && best>(Collections.min(unready.keySet()))) {
			currentLocation=unready.get(Collections.min(unready.keySet())).getLoc();
			currentCost=engine.getCost(currentLocation);
			for (CityLocation g : findAdjRoads(currentLocation)) {
				if (!found.contains(g)) {
					this.found.add(g);
					int keyi=16384*evalfunc(currentLocation,goal)+g.y;
					unready.put(keyi,new SpecifiedTile(g,currentLocation,false));
					mapBack.put(g, keyi);
				} else {
					if (ready.containsKey(g)) {
						int c=evalfunc(g, goal)+currentCost+ready.get(ready.get(g).getPred()).getCosts();
						if (ready.get(g).getCosts()<=c) {
							ready.put(g, new SpecifiedTile(c,currentLocation,true));
						}
					} else {
						int keyi=16384*evalfunc(currentLocation,goal)+g.y;
						if (keyi<= (int) (mapBack.get(g)/16384)) {
							unready.put(keyi,new SpecifiedTile(g,currentLocation,false));
							mapBack.put(g, keyi);
						}
					}
				}
			}
		}
		if (best==200) {
			return -1;
		}
		return best;
	}
	
	private HashSet<CityLocation> findAdjRoads(CityLocation loc) {
		HashSet<CityLocation> ret=new HashSet<CityLocation>();
		for (int dir=0;dir<4;dir++) {
			if (engine.onMap(loc,dir)) {
				ret.add(Micropolis.goToAdj(loc,dir));
			}
		}
		return ret;
	}
	
	/**
	 * finds out if there are streets next to our zone and return a HashMap with default values
	 * @param pos zone center
	 * @return keys are the streets next to the zone, values are default
	 */
	
	public HashMap<CityLocation,SpecifiedTile> findPeriphereRoad(CityLocation pos){
		char tiletype;
		HashMap<CityLocation,SpecifiedTile> ret=new HashMap<CityLocation,SpecifiedTile>();
		tiletype=engine.getTile(pos.x, pos.y);
		int dimension; //height (and so width) of the tile
		if(tiletype==716){       //if tiletype==AIRPORT -> see TileConstants  needs to be changed for some new buildings potentially!!!
			dimension=4;
		}else{
			dimension=3;
		}
		/*if (dimension==3){ //need to change isRoad in TileConstants
			if (engine.onMap(new CityLocation(pos.x-2,pos.y-1))&&TileConstants.isRoadAny(engine.getTile(pos.x-2, pos.y-1))){  
				ret.put(new CityLocation(pos.x-2,pos.y-1),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x-2, pos.y))){  
				ret.put(new CityLocation(pos.x-2,pos.y),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x-2, pos.y+1))){  
				ret.put(new CityLocation(pos.x-2,pos.y+1),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x-1, pos.y-2))){  
				ret.put(new CityLocation(pos.x-1,pos.y-2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x-1, pos.y+2))){  
				ret.put(new CityLocation(pos.x-1,pos.y+2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x, pos.y-2))){  
				ret.put(new CityLocation(pos.x,pos.y-2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x, pos.y+2))){  
				ret.put(new CityLocation(pos.x,pos.y+2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x+1, pos.y-2))){  
				ret.put(new CityLocation(pos.x+1,pos.y-2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x+1, pos.y+2))){  
				ret.put(new CityLocation(pos.x+1,pos.y+2),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x+2, pos.y-1))){  
				ret.put(new CityLocation(pos.x+2,pos.y-1),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x+2, pos.y))) {  
				ret.put(new CityLocation(pos.x+2,pos.y),new SpecifiedTile());
			}
			if (TileConstants.isRoadAny(engine.getTile(pos.x+2, pos.y+1))){  
				ret.put(new CityLocation(pos.x+2,pos.y+1),new SpecifiedTile());
			}
		}
		if(dimension==4){
			
			
		}*/
		
		for(int i=-1; i<dimension-1;i++){
			if (engine.onMap(new CityLocation(pos.x-2,pos.y+i))&&TileConstants.isRoadAny(engine.getTile(pos.x-2, pos.y+i))){  
				ret.put(new CityLocation(pos.x-2,pos.y+i),new SpecifiedTile());
			}
			if (engine.onMap(new CityLocation(pos.x+dimension-1,pos.y+i))&&TileConstants.isRoadAny(engine.getTile(pos.x+2, pos.y+i))){  
				ret.put(new CityLocation(pos.x+dimension-1,pos.y+i),new SpecifiedTile());
			}
			if (engine.onMap(new CityLocation(pos.x+i,pos.y-2))&&TileConstants.isRoadAny(engine.getTile(pos.x+i, pos.y-2))){  
				ret.put(new CityLocation(pos.x+i,pos.y-2),new SpecifiedTile());
			}
			if (engine.onMap(new CityLocation(pos.x+i,pos.y+dimension-1))&&TileConstants.isRoadAny(engine.getTile(pos.x+i, pos.y+2))){  
				ret.put(new CityLocation(pos.x+i,pos.y+dimension-1),new SpecifiedTile());
			}
		}
		
		return ret;
	}
	/**
	 *  increases the traffic on a given tile by value
	 * @param pos
	 * @param value is the zone type of the tile
	 */
	public void makeTraffic(CityLocation pos, int value){
		
	}
	/**
	 * caped at 32 767
	 * @param start
	 * @param finish
	 * @return
	 */
	
	public static int evalfunc(CityLocation start, HashSet<CityLocation> finish){
		int ret=32767;
		for (CityLocation g : finish) {
			ret=Math.min(ret,Math.abs(start.x-g.x)+Math.abs(start.y-g.y)+1);
		}
		return ret;
	}
	
	
	
	
	
}
