// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;

/**
 * Enumerates the various tools that can be applied to the map by the user.
 * Call the tool's apply() method to actually use the tool on the map.
 */
public enum MicropolisTool
{
    BULLDOZER(1, 1, 0),
    WIRE(1, 5, 0),   //cost=25 for underwater
    ROADS(1, 10, 0), //cost=50 for over water
    BIGROADS(1, 20, 0), //cost=xx for over water
    RAIL(1, 25, 0),  //cost=100 for underwater
    STATION(1, 200, 0),
    RESIDENTIAL(3, 100, 0),
    COMMERCIAL(3, 100, 0),
    INDUSTRIAL(3, 100, 0),
    FIRE(3, 500, 0),
    POLICE(3, 500, 0),
    STADIUM(4, 5000, 20),
    PARK(1, 10, 0),
    SEAPORT(4, 3000, 0),
    POWERPLANT(4, 3000, 0),
    NUCLEAR(4, 5000, 0),
    AIRPORT(6, 10000, 1000),
    SCHOOL(3, 500, 100),
    MUSEUM(3, 500, 50),
    UNIA(3, 1000, 0),
    UNIB(3, 1000, 0),
    OPENAIR(6, 500, 1500),
    CITYHALL(3, 1500, 0),
    QUERY(1, 0, 0),
    BIGPARK(3, 100, 500),
    SOLAR(4, 4000, 0),
    WIND(1, 325, 0);

	int size;
	int cost;
    int minPopulation;

    private MicropolisTool(int size, int cost, int minPopulation) {
		this.size = size;
		this.cost = cost;
        this.minPopulation = minPopulation;
    }

	public int getWidth()
	{
		return size;
	}

	public int getHeight()
	{
		return getWidth();
	}

	public ToolStroke beginStroke(Micropolis engine, int xpos, int ypos)
	{
		if (this == BULLDOZER) {
			return new Bulldozer(engine, xpos, ypos);
		}
		else if (this == WIRE ||
			this == ROADS ||
			this == BIGROADS ||
			this == RAIL ||
			this == STATION ) {
			return new RoadLikeTool(engine, this, xpos, ypos);
		}
		else {
			return new ToolStroke(engine, this, xpos, ypos);
		}
	}

	public ToolResult apply(Micropolis engine, int xpos, int ypos)
	{
		return beginStroke(engine, xpos, ypos).apply();
	}

	/**
	 * This is the cost displayed in the GUI when the tool is selected.
	 * It does not necessarily reflect the cost charged when a tool is
	 * applied, as extra may be charged for clearing land or building
	 * over or through water.
	 */
	public int getToolCost()
	{
		return cost;
	}

    public int getMinPopulation() {
        return minPopulation;
    }
}
