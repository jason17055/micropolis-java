// This file is part of DiverCity
// DiverCity is based on MicropolisJ
// Copyright (C) 2014 Arne Roland, Benjamin Kretz, Estela Gretenkord i Berenguer, Fabian Mett, Marvin Becker, Tom Brewe, Tony Schwedek, Ullika Scholz, Vanessa Schreck
// Copyright (C) 2013 Jason Long for MicropolisJ
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import static micropolisj.engine.TileConstants.*;
import micropolisj.engine.techno.BuildingTechnology;
import micropolisj.engine.techno.Technology;


public class ToolStroke
{
	final Micropolis city;
	final MicropolisTool tool;
	int xpos;
	int ypos;
	int xdest;
	int ydest;
	boolean inPreview;

	ToolStroke(Micropolis city, MicropolisTool tool, int xpos, int ypos)
	{
		this.city = city;
		this.tool = tool;
		this.xpos = xpos;
		this.ypos = ypos;
		this.xdest = xpos;
		this.ydest = ypos;
	}

	public final ToolPreview getPreview()
	{
		ToolEffect eff = new ToolEffect(city);
		inPreview = true;
		try {
			applyArea(eff);
		}
		finally {
			inPreview = false;
		}
		return eff.preview;
	}

	public final ToolResult apply()
	{
		ToolEffect eff = new ToolEffect(city);
		applyArea(eff);
        return eff.apply();

    }

	protected void applyArea(ToolEffectIfc eff)
	{
		CityRect r = getBounds();

		for (int i = 0; i < r.height; i += tool.getHeight()) {
			for (int j = 0; j < r.width; j += tool.getWidth()) {
				apply1(new TranslatedToolEffect(eff, r.x+j, r.y+i));
			}
		}
	}

	boolean apply1(ToolEffectIfc eff)
	{

		switch (tool)
		{
		case PARK:
			return applyParkTool(eff);

		case RESIDENTIAL:
			return applyZone(eff, RESCLR);

		case COMMERCIAL:
			return applyZone(eff, COMCLR);

		case INDUSTRIAL:
			return applyZone(eff, INDCLR);

		case FIRE:
			return applyZone(eff, FIRESTATION);

		case POLICE:
			return applyZone(eff, POLICESTATION);

		case POWERPLANT:
			return applyZone(eff, POWERPLANT);

		case STADIUM:
            return applyZone(eff, STADIUM);

		case SCHOOL:
			return applyZone(eff, SCHOOLBUILDING);

		case MUSEUM:
				return applyZone(eff, MUSEUMBUILDING);

            case UNIA:
                return applyZone(eff, UNIABUILDING);

            case UNIB:
            return applyZone(eff, UNIBBUILDING);

            case CITYHALL:
            return applyZone(eff, CITYHALLBUILDING);

		case OPENAIR:
            return applyZone(eff, OPENAIRBUILDING);

		case SEAPORT:
			return applyZone(eff, PORT);

            case BIGPARK:
            return applyZone(eff, BIGPARKBUILDING);

            case NUCLEAR:
			return applyZone(eff, NUCLEAR);

		case SOLAR:
			return applyZone(eff, SOLAR);
			
		case WIND:
			return applyZone(eff, WIND);			
			
		case AIRPORT:
            return applyZone(eff, AIRPORT);


            default:
			// not expected
			throw new Error("unexpected tool: "+tool);
		}
	}

	public void dragTo(int xdest, int ydest)
	{
		this.xdest = xdest;
		this.ydest = ydest;
	}

	public CityRect getBounds()
	{
		CityRect r = new CityRect();

		r.x = xpos;
		if (tool.getWidth() >= 3) {
			r.x--;
		}
		if (xdest >= xpos) {
			r.width = ((xdest-xpos) / tool.getWidth() + 1) * tool.getWidth();
		}
		else {
			r.width = ((xpos-xdest) / tool.getWidth() + 1) * tool.getHeight();
			r.x += tool.getWidth() - r.width;
		}

		r.y = ypos;
		if (tool.getHeight() >= 3) {
			r.y--;
		}
		if (ydest >= ypos) {
			r.height = ((ydest-ypos) / tool.getHeight() + 1) * tool.getHeight();
		}
		else {
			r.height = ((ypos-ydest) / tool.getHeight() + 1) * tool.getHeight();
			r.y += tool.getHeight() - r.height;
		}

		return r;
	}

	public CityLocation getLocation()
	{
		return new CityLocation(xpos, ypos);
	}

	boolean applyZone(ToolEffectIfc eff, int base)
	{
		assert isZoneCenter(base);

		TileSpec.BuildingInfo bi = Tiles.get(base).getBuildingInfo();
		if (bi == null) {
			throw new Error("Cannot applyZone to #"+base);
		}

		int cost = tool.getToolCost();

        boolean canBuild = true;
		for (int rowNum = 0; rowNum < bi.height; rowNum++) {
			for (int columnNum = 0; columnNum < bi.width; columnNum++)
			{
				int tileValue = eff.getTile(columnNum, rowNum);
				tileValue = tileValue & LOMASK;

				if (tileValue != DIRT) {
					if (city.autoBulldoze && canAutoBulldozeZ((char)tileValue)) {
						cost++;
					}
					else {
						canBuild = false;
					}
				}
			}
		}
		if (!canBuild) {
			eff.toolResult(ToolResult.UH_OH);
			return false;
		}

		eff.spend(cost);
        eff.setTool(tool);

		int i = 0;
		for (int rowNum = 0; rowNum < bi.height; rowNum++)
		{
			for (int columnNum = 0; columnNum < bi.width; columnNum++)
			{
				eff.setTile(columnNum, rowNum, (char) bi.members[i]);
				i++;
			}
		}

		fixBorder(eff, bi.width, bi.height);
		return true;
	}

	//compatible function
	void fixBorder(int left, int top, int right, int bottom)
	{
		ToolEffect eff = new ToolEffect(city, left, top);
		fixBorder(eff, right+1-left, bottom+1-top);
		eff.apply();
	}

	void fixBorder(ToolEffectIfc eff, int width, int height)
	{
		for (int x = 0; x < width; x++)
		{
			fixZone(new TranslatedToolEffect(eff, x, 0));
			fixZone(new TranslatedToolEffect(eff, x, height-1));
		}
		for (int y = 1; y < height - 1; y++)
		{
			fixZone(new TranslatedToolEffect(eff, 0, y));
			fixZone(new TranslatedToolEffect(eff, width-1, y));
		}
	}

	boolean applyParkTool(ToolEffectIfc eff)
	{
		int cost = tool.getToolCost();

		if (eff.getTile(0, 0) != DIRT) {
			// some sort of bulldozing is necessary
			if (!city.autoBulldoze) {
				eff.toolResult(ToolResult.UH_OH);
				return false;
			}

			//FIXME- use a canAutoBulldoze-style function here
			if (isRubble(eff.getTile(0, 0))) {
				// this tile can be auto-bulldozed
				cost++;
			}
			else {
				// cannot be auto-bulldozed
				eff.toolResult(ToolResult.UH_OH);
				return false;
			}
		}

		int z = inPreview ? 0 : city.PRNG.nextInt(5);
		int tile;
		if (z < 4) {
			tile = WOODS2 + z;
		} else {
			tile = FOUNTAIN;
		}

		eff.spend(cost);
		eff.setTile(0, 0, tile);
        eff.setTool(tool);

		return true;
	}

	protected void fixZone(int xpos, int ypos)
	{
		ToolEffect eff = new ToolEffect(city, xpos, ypos);
		fixZone(eff);
		eff.apply();
	}

	protected void fixZone(ToolEffectIfc eff)
	{

		fixSingle(eff);
		// "fix" the cells to the north, west, east, and south
		fixSingle(new TranslatedToolEffect(eff, 0, -1));
		fixSingle(new TranslatedToolEffect(eff, -1, 0));
		fixSingle(new TranslatedToolEffect(eff, 1, 0));
		fixSingle(new TranslatedToolEffect(eff, 0, 1));
		


	}

	private void fixSingle(ToolEffectIfc eff)
	{
		int tile = eff.getTile(0, 0);
		int me = TileConstants.roadType(tile);

		
			int[] dir=new int[]{TileConstants.roadType(eff.getTile(-1, 0)),TileConstants.roadType(eff.getTile(0, 1)),TileConstants.roadType(eff.getTile(1, 0)),TileConstants.roadType(eff.getTile(0, -1))};
			int bigRoadCount=0;
			int roadCount=0;
			int railCount=0;
			for (int i : dir) {
				if (i>=3) {
					railCount++;
				} else if (i%4==1) { //crossovers are calculated in a strange way
					roadCount++;
				} else if (i%4==2) {
					bigRoadCount++;
				}
			}
			int anyRoadCount=roadCount+bigRoadCount;
			// cleanup road
			int adjTile = 0;
			// check road to north
			if (isWireDynamic(tile)) {
				// Cleanup Wire
				adjTile = 0;

				// check wire to north
				if (wireConnectsSouth(eff.getTile(0, -1)))
				{
					adjTile |= 1;
				}

				// check wire to east
				if (wireConnectsWest(eff.getTile(1, 0)))
				{
					adjTile |= 2;
				}

				// check wire to south
				if (wireConnectsNorth(eff.getTile(0, 1)))
				{
					adjTile |= 4;
				}

				// check wire to west
				if (wireConnectsEast(eff.getTile(-1, 0)))
				{
					adjTile |= 8;
				}

				eff.setTile(0, 0, WireTable[adjTile]);
			}
			if (!isUndynamicWire(tile)) {
				if (me==1 || me==2) {
					for (int i=0;i<4;i++) {
						if (dir[i]==1 || dir[i]==2) {
							adjTile |= (int) Math.pow(2, 3-i);
						}
					}
					if (me==1) {
						eff.setTile(0, 0, RoadTable[adjTile]);
					} else {
						if ((anyRoadCount!=2 || roadCount==0) && !(anyRoadCount==1 && roadCount==1)) {
							eff.setTile(0, 0, BigRoadTable[adjTile]);
						} else {
							if (roadCount==1 && bigRoadCount==1) {
								if ((dir[0]%4==2 && dir[2]%4==1)) {
									eff.setTile(0, 0, 1221);
								} else if (dir[1]%4==2 && dir[3]%4==1) {
									eff.setTile(0, 0, 1223);
								} else if (dir[0]%4==1 && dir[2]%4==2) {
									eff.setTile(0, 0, 1222);
								} else if (dir[1]%4==1 && dir[3]%4==2) {
									eff.setTile(0, 0, 1224);
								}
							} else if (anyRoadCount==1 && roadCount==1) {
								if (dir[2]%4==1) {
									eff.setTile(0, 0, 1221);
								} else if (dir[3]%4==1) {
									eff.setTile(0, 0, 1223);
								} else if (dir[0]%4==1) {
									eff.setTile(0, 0, 1222);
								} else if (dir[1]%4==1) {
									eff.setTile(0, 0, 1224);
								}
							}
						}
					}
				}
				if (me==3 || me==4) {
					for (int i=0;i<4;i++) {
						if (dir[i]==3 || dir[i]==4) {
							adjTile |= (int) Math.pow(2, 3-i);
						}
					}
					eff.setTile(0, 0, RailTable[adjTile]);
				}
				if (me==5|| me==6) {
					if (anyRoadCount!=0 || (anyRoadCount%2!=0 && railCount%2!=0)) {
						for (int i=0;i<4;i++) {
							if (dir[i]==1 || dir[i]==2) {
								if (i%2==0) {
									eff.setTile(0, 0, 238);
									return;
								} else {
									eff.setTile(0, 0, 237);
									return;
								}
							}
						}
						
					} else {
						if (railCount!=0) {
							for (int i=0;i<4;i++) {
								if (dir[i]==3 || dir[i]==4) {
									if (i%2==0) {
										eff.setTile(0, 0, 237);
										return;
									} else {
										eff.setTile(0, 0, 238);
										return;
									}
								}
							}
						}
					}
					eff.setTile(0, 0, 237);
				}
			}

		return;
	}
	
	private boolean isUndynamicWire(int tile) {
		return TileConstants.isConductive(tile) && !isWireDynamic(tile);
	}
}
