package micropolisj.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import micropolisj.engine.*;

public class SwitchImage extends TileImage
{
	public ArrayList<Case> cases = new ArrayList<Case>();

	public static class Case
	{
		public final TileCondition condition;
		public final TileImage img;

		public Case(TileCondition cond, TileImage img)
		{
			this.condition = cond;
			this.img = img;
		}

		public boolean matches(Micropolis city, CityLocation loc)
		{
			return condition.matches(city, loc);
		}
	}

	public void addCase(TileCondition condition, TileImage image)
	{
		cases.add(new Case(condition, image));
	}

	private Case getDefaultCase()
	{
		assert !cases.isEmpty();
		Case c = cases.get(cases.size()-1);
		assert c.condition == TileCondition.ALWAYS;

		return c;
	}

	public TileImage getDefaultImage()
	{
		assert !cases.isEmpty();
		return getDefaultCase().img;
	}

	public boolean hasMultipleCases()
	{
		return cases.size() > 1;
	}

	@Override
	public Dimension getBounds() {
		return getDefaultImage().getBounds();
	}

	@Override
	public Dimension getSize() {
		return getDefaultImage().getSize();
	}

	@Override
	public void drawFragment(Graphics2D gr, int srcX, int srcY, int srcWidth, int srcHeight) {
		// Warning: drawing an unrealized image
		getDefaultImage().drawFragment(gr, srcX, srcY, srcWidth, srcHeight);
	}
}
