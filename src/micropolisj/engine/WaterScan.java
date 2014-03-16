package micropolisj.engine;

import java.util.*;

class WaterScan
{
	final Micropolis city;

	WaterScan(Micropolis city)
	{
		this.city = city;
	}

	static int Dx[] = new int[] { -1, 0, 1, 0 };
	static int Dy[] = new int[] { 0, -1, 0, 1 };

	boolean [][] seen;
	List<WaterPart> parts = new ArrayList<WaterPart>();
	WaterPart source = new WaterPart();

	public void waterScan()
	{
		this.seen = new boolean[city.getHeight()][city.getWidth()];

		for (int y = 0; y < city.getHeight(); y++) {
			for (int x = 0; x < city.getWidth(); x++) {
				if (!seen[y][x] && isRiverPart(city.getTile(x, y))) {
					makeWaterPart(x, y);
				}
			}
		}

		connectParts();

		// start from the water source
		WaterPart p = findWaterSink(source);
		if (p.body != null) {
			addWater(p);
		}
	}

	WaterPart findWaterSink(WaterPart src)
	{
		if (src.outFlows.isEmpty()) {
			return src;
		}

		for (WaterFlow f : src.outFlows)
		{
			WaterPart c = findWaterSink(f.to);
			if (c != null) {
				return c;
			}
		}
		return null;
	}

	void addWater(WaterPart p)
	{
		ArrayList<CityLocation> lowerLand = new ArrayList<CityLocation>();
		ArrayList<CityLocation> equalLand = new ArrayList<CityLocation>();
		ArrayList<CityLocation> raisable = new ArrayList<CityLocation>();

		for (CityLocation loc : p.body)
		{
			boolean isRaisable = false;
			if (isRiverInflow(city.getTile(loc.x, loc.y))) {
				isRaisable = true;
			}

			// check neighbors for non-water tiles
			for (int i = 0; i < Dx.length; i++) {
				int x = loc.x + Dx[i];
				int y = loc.y + Dy[i];
				if (!city.testBounds(x, y)) {
					continue;
				}

				short el = city.getTileElevation(x, y);
				if (el < p.elevation && !isRiverPart(city.getTile(x, y))) {
					lowerLand.add(new CityLocation(x, y));
				}
				else if (el == p.elevation && !isRiverPart(city.getTile(x, y))) {
					equalLand.add(new CityLocation(x, y));
				}
				else if (el > p.elevation && isRiverPart(city.getTile(x, y))) {
					isRaisable = true;
				}
			}

			if (isRaisable) {
				raisable.add(loc);
			}
		}

		if (!lowerLand.isEmpty()) {
			int i = city.PRNG.nextInt(lowerLand.size());
			CityLocation aLoc = lowerLand.get(i);

			overflowTo(p, aLoc);
			return;
		}

		if (!equalLand.isEmpty()) {
			int i = city.PRNG.nextInt(equalLand.size());
			CityLocation aLoc = equalLand.get(i);

			expandTo(p, aLoc);
			return;
		}

		if (!raisable.isEmpty()) {
			int i = city.PRNG.nextInt(raisable.size());
			CityLocation aLoc = raisable.get(i);

			raiseWater(p, aLoc);
			return;
		}

		System.out.println("Oops, cannot grow river");
	}

	void overflowTo(WaterPart p, CityLocation aLoc)
	{
		assert p != null && !p.body.contains(aLoc);
		assert city.testBounds(aLoc.x, aLoc.y);
		assert !isRiverPart(city.getTile(aLoc.x, aLoc.y));
		assert city.getTileElevation(aLoc.x, aLoc.y) < p.elevation;

		System.out.println("adding lower-elevation river tile at "+aLoc.x+","+aLoc.y);
		city.setTile(aLoc.x, aLoc.y, (char) Tiles.load("river").tileNumber);
		seen[aLoc.y][aLoc.x] = true;

		// check neighbors for a same-elevation river tile
		WaterPart destPart = null;
		for (int i = 0; i < Dx.length; i++) {
			int x = aLoc.x + Dx[i];
			int y = aLoc.y + Dy[i];
			if (city.testBounds(x, y) &&
				isRiverPart(city.getTile(x, y)) &&
				city.getTileElevation(x, y) == city.getTileElevation(aLoc.x, aLoc.y)
			) {
				// this neighbor has same elevation, so the
				// new tile should join that water part
				destPart = findPart(new CityLocation(x, y));
				assert destPart != null;
				destPart.body.add(aLoc);
				break;
			}
		}

		if (destPart == null) {
			// have to make a new water part
			destPart = new WaterPart();
			destPart.elevation = city.getTileElevation(aLoc.x, aLoc.y);
			destPart.body = new HashSet<CityLocation>();
			destPart.body.add(aLoc);
			parts.add(destPart);
		}

		addFlow(p, destPart, 1);
	}

	void expandTo(WaterPart p, CityLocation aLoc)
	{
		assert p != null && !p.body.contains(aLoc);
		assert city.testBounds(aLoc.x, aLoc.y);
		assert !isRiverPart(city.getTile(aLoc.x, aLoc.y));
		assert city.getTileElevation(aLoc.x, aLoc.y) == p.elevation;

		System.out.println("adding same-elevation river tile at "+aLoc.x+","+aLoc.y);
		city.setTile(aLoc.x, aLoc.y, (char) Tiles.load("river").tileNumber);
		seen[aLoc.y][aLoc.x] = true;

		p.body.add(aLoc);

		// check whether this part is now adjacent to another
		// part with same elevation

		checkNeighborsAt(p, aLoc);
	}

	void checkNeighborsAt(WaterPart p, CityLocation aLoc)
	{
		for (int i = 0; i < Dx.length; i++) {
			CityLocation bLoc = new CityLocation(aLoc.x+Dx[i], aLoc.y+Dy[i]);
			WaterPart q = findPart(bLoc);
			if (q != null && q != p && q.elevation == p.elevation) {
				mergeParts(p, q);
			}
		}
	}

	void mergeParts(WaterPart p, WaterPart q)
	{
		assert p != null;
		assert q != null;
		assert p != q;
		assert p.elevation == q.elevation;

		assert parts.contains(q);

		parts.remove(q);
		p.body.addAll(q.body);
		for (WaterFlow f : q.outFlows) {
			addFlow(p, f.to, f.volume);
		}
	}

	void raiseWater(WaterPart p, CityLocation aLoc)
	{
		assert p != null && p.body.contains(aLoc);
		assert city.testBounds(aLoc.x, aLoc.y);
		assert isRiverPart(city.getTile(aLoc.x, aLoc.y));
		assert city.getTileElevation(aLoc.x, aLoc.y) == p.elevation;

		System.out.println("raising river at "+aLoc.x+","+aLoc.y);
		city.setTileElevation(aLoc.x, aLoc.y, (short)(p.elevation+1));
	}

	void makeWaterPart(int xpos, int ypos)
	{
		CityLocation start = new CityLocation(xpos, ypos);
		HashSet<CityLocation> body = new HashSet<CityLocation>();
		body.add(start);
		seen[ypos][xpos] = true;
		Stack<CityLocation> Q = new Stack<CityLocation>();
		Q.add(start);

		WaterPart part = new WaterPart();
		part.elevation = city.getTileElevation(xpos, ypos);

		while (!Q.isEmpty())
		{
			CityLocation loc = Q.pop();

			if (city.getTile(loc.x, loc.y) == Tiles.load("river_source").tileNumber) {
				addFlow(source, part, 1);
			}

			for (int i = 0; i < Dx.length; i++) {
				int x = loc.x + Dx[i];
				int y = loc.y + Dy[i];

				if (city.testBounds(x, y) &&
					!seen[y][x] &&
					isRiverPart(city.getTile(x, y)) &&
					city.getTileElevation(x, y) == part.elevation
					)
				{
					CityLocation loc1 = new CityLocation(x, y);
					if (!body.contains(loc1)) {
						seen[y][x] = true;
						body.add(loc1);
						Q.add(loc1);
					}
				}
			}
		}

		part.body = body;
		parts.add(part);
	}

	void addFlow(WaterPart from, WaterPart to, int volume)
	{
		for (WaterFlow flow : from.outFlows) {
			if (flow.to == to) {
				flow.volume += volume;
				return;
			}
		}

		WaterFlow f = new WaterFlow(from, to);
		f.volume = volume;
		from.outFlows.add(f);
	}

	void connectParts()
	{
		for (WaterPart part : parts) {
			connectParts(part);
		}
	}

	void connectParts(WaterPart part)
	{
		for (CityLocation loc : part.body) {
			for (int i = 0; i < Dx.length; i++) {
				int x = loc.x + Dx[i];
				int y = loc.y + Dy[i];

				if (city.testBounds(x, y) &&
					isRiverPart(city.getTile(x, y)) &&
					city.getTileElevation(x, y) < part.elevation
					)
				{
					CityLocation oLoc = new CityLocation(x, y);
					WaterPart oPart = findPart(oLoc);
					assert oPart != null;

					addFlow(part, oPart, 1);
				}
			}
		}
	}

	WaterPart findPart(CityLocation loc)
	{
		for (WaterPart part : parts) {
			if (part.body.contains(loc)) {
				return part;
			}
		}
		return null;
	}

	static class WaterPart
	{
		Set<CityLocation> body;
		//int inVolume;
		short elevation;
		Collection<WaterFlow> outFlows = new ArrayList<WaterFlow>();
	}

	static class WaterFlow
	{
		final WaterPart from;
		final WaterPart to;
		int volume;

		WaterFlow(WaterPart from, WaterPart to)
		{
			this.from = from;
			this.to = to;
		}
	}

	static boolean isRiverInflow(int tileNumber)
	{
		return (
		tileNumber == Tiles.load("river_source").tileNumber
		);
	}

	static boolean isRiverPart(int tileNumber)
	{
		return (
		tileNumber == Tiles.load("river").tileNumber ||
		tileNumber == Tiles.load("river_source").tileNumber ||
		tileNumber == Tiles.load("river_flow_north").tileNumber ||
		tileNumber == Tiles.load("river_flow_south").tileNumber ||
		tileNumber == Tiles.load("river_flow_east").tileNumber ||
		tileNumber == Tiles.load("river_flow_west").tileNumber
		);
	}
}
