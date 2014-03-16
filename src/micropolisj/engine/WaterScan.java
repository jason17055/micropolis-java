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
		calculateInFlows();

		Set<CityLocation> waterAdd = new HashSet<CityLocation>();
		Set<CityLocation> waterSub = new HashSet<CityLocation>();

		for (WaterPart p : parts) {
			int netFlow = p.getNetFlow();
			if (netFlow > 0) {
				
				Collection<CityLocation> locs = pickLocationOfWaterIncrease(p, netFlow);
				for (CityLocation l : locs) {
					waterAdd.add(l);
				}
			}
			else if (netFlow < 0) {
				System.out.println("water should lower "+netFlow);
			}
		}

		if (!waterAdd.isEmpty()) {
			System.out.printf("adding water in %d locations\n", waterAdd.size());
		}

		for (CityLocation loc : waterAdd) {
			if (waterSub.contains(loc)) {
				waterSub.remove(loc);
				continue;
			}
			addWater(loc);
		}

		for (CityLocation loc : waterSub) {
			removeWater(loc);
		}
	}

	void calculateInFlows()
	{
		for (WaterPart p : parts) {
			p.inFlowVolume = 0;
		}
		applyOutFlow(source);
		for (WaterPart p : parts) {
			applyOutFlow(p);
		}
	}

	void applyOutFlow(WaterPart p)
	{
		for (WaterFlow f : p.outFlows) {
			f.to.inFlowVolume += f.volume;
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

	Collection<CityLocation> pickLocationOfWaterIncrease(WaterPart p, int count)
	{
		assert !p.body.isEmpty();

		// land of lower elevation neighboring this body
		ArrayList<CityLocation> lowerLand = new ArrayList<CityLocation>();
		// land of equal elevation neighboring this body
		ArrayList<CityLocation> equalLand = new ArrayList<CityLocation>();
		// water in this body that neighbors higher water
		ArrayList<CityLocation> raisable = new ArrayList<CityLocation>();
		// remaining water in this body
		ArrayList<CityLocation> otherWater = new ArrayList<CityLocation>();

		HashSet<CityLocation> processedNeighbors = new HashSet<CityLocation>();

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
				CityLocation nLoc = new CityLocation(x, y);
				if (processedNeighbors.contains(nLoc)) {
					continue;
				}
				processedNeighbors.add(nLoc);

				short el = city.getTileElevation(x, y);
				if (el < p.elevation && !isRiverPart(city.getTile(x, y))) {
					lowerLand.add(nLoc);
				}
				else if (el == p.elevation && !isRiverPart(city.getTile(x, y))) {
					equalLand.add(nLoc);
				}
				else if (el > p.elevation && isRiverPart(city.getTile(x, y))) {
					isRaisable = true;
				}
			}

			if (isRaisable) {
				raisable.add(loc);
			}
			else {
				otherWater.add(loc);
			}
		}

		ArrayList<CityLocation> list = new ArrayList<CityLocation>();
		while (list.size() < count && !lowerLand.isEmpty()) {
			int i = city.PRNG.nextInt(lowerLand.size());
			list.add(lowerLand.remove(i));
		}
		while (list.size() < count && !equalLand.isEmpty()) {
			int i = city.PRNG.nextInt(equalLand.size());
			list.add(equalLand.remove(i));
		}
		while (list.size() < count && !raisable.isEmpty()) {
			int i = city.PRNG.nextInt(raisable.size());
			list.add(raisable.remove(i));
		}
		while (list.size() < count && !otherWater.isEmpty()) {
			int i = city.PRNG.nextInt(otherWater.size());
			list.add(otherWater.remove(i));
		}
		return list;
	}

	/**
	 * Add one unit of water at a particular location.
	 * @see #changeToWater, #raiseWater.
	 */
	void addWater(CityLocation loc)
	{
		if (!isRiverPart(city.getTile(loc.x, loc.y)))
		{
			changeToWater(loc);
		}
		else
		{
			raiseWater(loc);
		}
	}

	void removeWater(CityLocation loc)
	{
		//TODO
	}

	/**
	 * Turn a land tile into shallow water.
	 */
	void changeToWater(CityLocation aLoc)
	{
		assert city.testBounds(aLoc.x, aLoc.y);
		assert !isRiverPart(city.getTile(aLoc.x, aLoc.y));

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
			destPart.body.add(aLoc);
			parts.add(destPart);
		}

		//TODO
		//addFlow(p, destPart, 1);
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

	WaterPart splitPart(WaterPart p, CityLocation loc)
	{
		assert p.body.contains(loc);

		if (p.body.size() == 1 && p.body.contains(loc)) {
			return p;
		}

		p.body.remove(loc);

		WaterPart q = new WaterPart();
		q.body.add(loc);
		q.elevation = p.elevation;
		parts.add(q);

		return q;
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

	/**
	 * Turn a water tile into a slightly higher water tile.
	 */
	void raiseWater(CityLocation aLoc)
	{
		assert city.testBounds(aLoc.x, aLoc.y);
		assert isRiverPart(city.getTile(aLoc.x, aLoc.y));

		WaterPart p = findPart(aLoc);
		assert city.getTileElevation(aLoc.x, aLoc.y) == p.elevation;

		WaterPart q = splitPart(p, aLoc);
		q.elevation++;
		city.setTileElevation(aLoc.x, aLoc.y, (short) q.elevation);
		checkNeighborsAt(q, aLoc);
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
		Set<CityLocation> body = new HashSet<CityLocation>();
		short elevation;
		Collection<WaterFlow> outFlows = new ArrayList<WaterFlow>();
		int inFlowVolume;

		int getNetFlow()
		{
			return inFlowVolume - getOutFlowVolume();
		}

		int getOutFlowVolume()
		{
			int sum = 0;
			for (WaterFlow f : outFlows) {
				sum += f.volume;
			}
			return sum;
		}
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
