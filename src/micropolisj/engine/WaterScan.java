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
		System.out.println("I am here");
	}

	void makeWaterPart(int xpos, int ypos)
	{
		CityLocation start = new CityLocation(xpos, ypos);
		HashSet<CityLocation> body = new HashSet<CityLocation>();
		body.add(start);
		seen[ypos][xpos] = true;
		Stack<CityLocation> Q = new Stack<CityLocation>();
		Q.add(start);

		short startEl = city.getTileElevation(xpos, ypos);
		int countSprings = 0;

		while (!Q.isEmpty())
		{
			CityLocation loc = Q.pop();

			if (city.getTile(loc.x, loc.y) == Tiles.load("river_source").tileNumber) {
				countSprings++;
			}

			for (int i = 0; i < Dx.length; i++) {
				int x = loc.x + Dx[i];
				int y = loc.y + Dy[i];

				if (city.testBounds(x, y) &&
					!seen[y][x] &&
					isRiverPart(city.getTile(x, y)) &&
					city.getTileElevation(x, y) == startEl
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

		WaterPart part = new WaterPart();
		part.body = body;
		part.inVolume = countSprings;
		part.elevation = startEl;
		parts.add(part);
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

					if (!part.flowsTo.contains(oPart)) {
						part.flowsTo.add(oPart);
					}
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
		Collection<WaterPart> flowsTo = new ArrayList<WaterPart>();
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
