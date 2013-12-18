// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

class ElevationTool extends ToolStroke
{
	ElevationTool(Micropolis city, MicropolisTool tool, int xpos, int ypos)
	{
		super(city, tool, xpos, ypos);
	}

	@Override
	protected void applyArea(ToolEffectIfc eff)
	{
	}

	@Override
	public ToolResult apply()
	{
		CityRect r = getBounds();

		for (int i = 0; i < r.height; i += tool.getHeight()) {
			for (int j = 0; j < r.width; j += tool.getWidth()) {

				short el = city.getTileElevation(r.x+j, r.y+i);
				el += (tool == MicropolisTool.GROUND_RAISE ? 1 : -1);
				city.setTileElevation(r.x+j, r.y+i, el);
			}
		}
		return ToolResult.SUCCESS;
	}
}
