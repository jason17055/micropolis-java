// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

class RiverStartTool extends ToolStroke
{
	int magnitude;

	public RiverStartTool(Micropolis engine, int xpos, int ypos)
	{
		super(engine, MicropolisTool.RIVERSTART, xpos, ypos);
	}

	@Override
	public void dragTo(int xdest, int ydest)
	{
		magnitude = Math.abs(xdest-xpos) + Math.abs(ydest-ypos);
	}

	@Override
	boolean apply1(ToolEffectIfc eff)
	{
		for (int i = 0; i <= magnitude; i++) {
			eff.setTile(i, 0, RIVER);
		}
		return true;
	}
}
