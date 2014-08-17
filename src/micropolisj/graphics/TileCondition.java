package micropolisj.graphics;

import micropolisj.engine.*;

import static micropolisj.graphics.TileImage.DrawContext;

public class TileCondition
{
	String key;
	String value;

	public boolean matches(Micropolis city, CityLocation loc)
	{
		assert this.key.equals("tile-west"); //only supported one for now
		CityLocation nloc = new CityLocation(loc.x-1,loc.y);
		if (!city.testBounds(nloc.x, nloc.y)) {
			return false;
		}

		TileSpec ts = Tiles.get(city.getTile(nloc.x, nloc.y));
		return ts.name.equals(this.value);
	}

	public boolean test(DrawContext dc)
	{
		if (dc.city == null) {
			return false;
		}
		return matches(dc.city, dc.location);
	}

	public static TileCondition ALWAYS = new TileCondition() {
		@Override
		public boolean matches(Micropolis city, CityLocation loc) {
			return true;
		}
		@Override
		public boolean test(DrawContext dc) {
			return true;
		}
		};
}
