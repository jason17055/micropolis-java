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
		addWater(p);
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

			System.out.println("adding lower-elevation river tile at "+aLoc.x+","+aLoc.y);
			city.setTile(aLoc.x, aLoc.y, (char) Tiles.load("river").tileNumber);
			return;
		}

		if (!equalLand.isEmpty()) {
			int i = city.PRNG.nextInt(equalLand.size());
			CityLocation aLoc = equalLand.get(i);

			System.out.println("adding same-elevation river tile at "+aLoc.x+","+aLoc.y);
			city.setTile(aLoc.x, aLoc.y, (char) Tiles.load("river").tileNumber);
			return;
		}

		if (!raisable.isEmpty()) {
			int i = city.PRNG.nextInt(raisable.size());
			CityLocation aLoc = raisable.get(i);

			System.out.println("raising river at "+aLoc.x+","+aLoc.y);
			city.setTileElevation(aLoc.x, aLoc.y, (short)(p.elevation+1));
			return;
		}

		System.out.println("Oops, cannot grow river");
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
		int inVolume;
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
