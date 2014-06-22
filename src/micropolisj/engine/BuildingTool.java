// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class BuildingTool extends ToolStroke
{
	public BuildingTool(Micropolis engine, MicropolisTool tool, int xpos, int ypos)
	{
		super(engine, tool, xpos, ypos);
	}

	@Override
	public void dragTo(int xdest, int ydest)
	{
		this.xpos = xdest;
		this.ypos = ydest;
		this.xdest = xdest;
		this.ydest = ydest;
	}

	@Override
	boolean apply1(ToolEffectIfc eff)
	{
		switch (tool)
		{
		case FIRE:
			return applyZone(eff, Tiles.loadByOrdinal(FIRESTATION));

		case POLICE:
			return applyZone(eff, Tiles.loadByOrdinal(POLICESTATION));

		case POWERPLANT:
			return applyZone(eff, Tiles.loadByOrdinal(POWERPLANT));

		case STADIUM:
			return applyZone(eff, Tiles.loadByOrdinal(STADIUM));

		case SEAPORT:
			return applyZone(eff, Tiles.loadByOrdinal(PORT));

		case NUCLEAR:
			return applyZone(eff, Tiles.loadByOrdinal(NUCLEAR));

		case AIRPORT:
			return applyZone(eff, Tiles.loadByOrdinal(AIRPORT));

		default:
			// not expected
			throw new Error("unexpected tool: "+tool);
		}
	}
}
