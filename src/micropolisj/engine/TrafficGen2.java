package micropolisj.engine;

import java.util.*;
import static micropolisj.engine.TileConstants.*;
import static micropolisj.engine.Traffic.*;

public class TrafficGen2
{
	final Micropolis city;
	final CityLocation origin;

	int [][] dist;
	int [][] cameFrom;
	PriorityQueue<Path> Q = new PriorityQueue<Path>();

	static final int MAX_DIST = 80;

	private static class Path implements Comparable<Path>
	{
		int xpos; //city coordinates
		int ypos;
		int distance;

		Path(int xpos, int ypos, int distance)
		{
			this.xpos = xpos;
			this.ypos = ypos;
			this.distance = distance;
		}

		public int compareTo(Path rhs)
		{
			// ensure lowest distance sorts first
			return this.distance - rhs.distance;
		}
	}

	public TrafficGen2(Micropolis city, CityLocation origin)
	{
		this.city = city;
		this.origin = origin;
		this.dist = new int[MAX_DIST*2+1][MAX_DIST*2+1];
		this.cameFrom = new int[MAX_DIST*2+1][MAX_DIST*2+1];
	}

	protected void resetDist()
	{
		for (int[] A : dist) {
			Arrays.fill(A, Integer.MAX_VALUE);
		}
	}

	public int getDist(int xpos, int ypos)
	{
		int dx = xpos - origin.x;
		int dy = ypos - origin.y;
		return dist[MAX_DIST + dy][MAX_DIST + dx];
	}

	public int [] getPathTo(CityLocation loc)
	{
		return getPathTo(loc.x, loc.y);
	}

	public int [] getPathTo(int xpos, int ypos)
	{
		int tx = MAX_DIST + xpos - origin.x;
		int ty = MAX_DIST + ypos - origin.y;

		if (dist[ty][tx] == Integer.MAX_VALUE) {
			throw new Error("no path to that location");
		}

		// count how many steps are in this path
		int count = 0;
		while (!(tx == MAX_DIST && ty == MAX_DIST)) {
			count++;
			int dir = cameFrom[ty][tx];
			switch (dir) {
			case EAST: tx++; break;
			case WEST: tx--; break;
			case SOUTH: ty++; break;
			case NORTH: ty--; break;
			default:
				throw new Error("invalid direction");
			}

			if (count > 10*MAX_DIST) {
				throw new Error("infinite loop detected");
			}
		}

		int [] path = new int[count];
		tx = MAX_DIST + xpos - origin.x;
		ty = MAX_DIST + ypos - origin.y;
		while (!(tx == MAX_DIST && ty == MAX_DIST)) {
			int dir = cameFrom[ty][tx];
			path[--count] = (dir+2)%4;
			switch (dir) {
			case EAST: tx++; break;
			case WEST: tx--; break;
			case SOUTH: ty++; break;
			case NORTH: ty--; break;
			}
		}
		assert count == 0;
		return path;
	}

	protected void setCameFrom(int x, int y, int dir)
	{
		int dx = x - origin.x;
		int dy = y - origin.y;
		cameFrom[MAX_DIST + dy][MAX_DIST + dx] = dir;
	}

	protected void setDist(int x, int y, int d)
	{
		int dx = x - origin.x;
		int dy = y - origin.y;
		dist[MAX_DIST + dy][MAX_DIST + dx] = d;
	}

	void setStartingZone()
	{
		int baseTile = city.getTile(origin.x, origin.y) & LOMASK;
		TileSpec.BuildingInfo bi = Tiles.get(baseTile).getBuildingInfo();

		if (bi == null) {
			Q.add(new Path(origin.x, origin.y, 0));
			setDist(origin.x, origin.y, 0);
		}
		else {
			for (int y = origin.y-1; y < origin.y-1+bi.height; y++) {
				for (int x = origin.x-1; x < origin.x-1+bi.width; x++) {
					Q.add(new Path(x, y, 0));
					setDist(x, y, 0);
				}
			}
		}
	}

	public void prepare()
	{
		Q.clear();

		resetDist();
		setStartingZone();

		while (!Q.isEmpty())
		{
			Path cur = Q.remove();
			if (getDist(cur.xpos, cur.ypos) != cur.distance)
				continue; //already visited

			tryNeighbor(cur, cur.xpos+1, cur.ypos,   WEST);
			tryNeighbor(cur, cur.xpos,   cur.ypos+1, NORTH);
			tryNeighbor(cur, cur.xpos-1, cur.ypos,   EAST);
			tryNeighbor(cur, cur.xpos,   cur.ypos-1, SOUTH);
		}

		adjustEndZoneDistances();
	}

	void adjustEndZoneDistances()
	{
		for (int y = origin.y - MAX_DIST; y <= origin.y + MAX_DIST; y++) {
			for (int x = origin.x - MAX_DIST; x <= origin.x + MAX_DIST; x++) {
				if (city.testBounds(x,y) && isZoneCenter(city.getTile(x, y))) {
					adjustEndZoneDistance(x, y);
				}
			}
		}
	}

	void adjustEndZoneDistance(int xpos, int ypos)
	{
		int best = Integer.MAX_VALUE;

		int baseTile = city.getTile(xpos, ypos) & LOMASK;
		TileSpec.BuildingInfo bi = Tiles.get(baseTile).getBuildingInfo();

		if (bi == null) {
			return;
		}
		else {
			for (int y = ypos-1; y < ypos-1+bi.height; y++) {
				for (int x = xpos-1; x < xpos-1+bi.width; x++) {
					best = Math.min(best, getDist(x, y));
				}
			}
			setDist(xpos, ypos, best);
		}
	}

	void tryNeighbor(Path cur, int x, int y, int cameFrom)
	{
		if (!city.testBounds(x,y))
			return;
		if (x - origin.x < -MAX_DIST)
			return;
		if (y - origin.y < -MAX_DIST)
			return;
		if (x - origin.x > MAX_DIST)
			return;
		if (y - origin.y > MAX_DIST)
			return;

		int dist = cur.distance;
		char fromTile = city.getTile(cur.xpos, cur.ypos);
		char toTile = city.getTile(x, y);

		int adist;
		if (isRoad(toTile)) {
			int congestion = city.getTrafficDensity(x, y);
			adist = dist + (
				congestion < 128 ? 1 :
				congestion < 192 ? 2 :
				congestion < 256 ? 4 :
				congestion < 384 ? 6 :
				8);
		} else if (isRail(toTile)) {
			adist = dist + 1;
		} else if (!isOverWater(toTile)) {
			adist = dist + 8;
		} else {
			adist = Integer.MAX_VALUE;
		}

		if (adist < getDist(x, y)) {
			// improved path
			setDist(x, y, adist);
			setCameFrom(x, y, cameFrom);
			Q.add(new Path(x, y, adist));
		}
		else {
			// otherwise, we just found a less efficient
			// path to a place we already can get to...
			// so, do nothing.
		}
	}

	public static Set<CityLocation> convertPath(CityLocation startLoc, int [] path)
	{
		HashSet<CityLocation> mySet = new HashSet<CityLocation>();

		int x = startLoc.x;
		int y = startLoc.y;
		for (int dir : path) {
			switch (dir) {
			case EAST: x++; break;
			case WEST: x--; break;
			case SOUTH: y++; break;
			case NORTH: y--; break;
			}
			mySet.add(new CityLocation(x,y));
		}
		return mySet;		
	}

	public interface FitnessFunction
	{
		double fitness(int xpos, int ypos, int dist);
	}

	public CityLocation findBest(FitnessFunction f)
	{
		CityLocation best = null;
		double bestFitness = 0.0;

		for (int y = origin.y - MAX_DIST; y <= origin.y + MAX_DIST; y++) {
			for (int x = origin.x - MAX_DIST; x <= origin.x + MAX_DIST; x++) {
				int d = getDist(x, y);
				if (d == Integer.MAX_VALUE) continue;
				double v = f.fitness(x, y, d);
				if (v > bestFitness) {
					best = new CityLocation(x, y);
					bestFitness = v;
				}
			}
		}

		return best;
	}
}
