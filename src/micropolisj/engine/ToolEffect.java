// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import micropolisj.engine.techno.BuildingTechnology;

import static micropolisj.engine.TileConstants.CLEAR;

class ToolEffect implements ToolEffectIfc
{
	final Micropolis city;
	final ToolPreview preview;
	final int originX;
	final int originY;

	ToolEffect(Micropolis city)
	{
		this(city, 0, 0);
	}

	ToolEffect(Micropolis city, int xpos, int ypos)
	{
		this.city = city;
		this.preview = new ToolPreview();
		this.originX = xpos;
		this.originY = ypos;
	}

	//implements ToolEffectIfc
	public int getTile(int dx, int dy)
	{
		int c = preview.getTile(dx, dy);
		if (c != CLEAR) {
			return c;
		}

		if (city.testBounds(originX + dx, originY + dy)) {
			return city.getTile(originX + dx, originY + dy);
		}
		else {
			// tiles outside city's boundary assumed to be
			// tile #0 (dirt).
			return 0;
		}
	}

	//implements ToolEffectIfc
	public void makeSound(int dx, int dy, Sound sound)
	{
		preview.makeSound(dx, dy, sound);
	}

	//implements ToolEffectIfc
	public void setTile(int dx, int dy, int tileValue)
	{
		preview.setTile(dx, dy, tileValue);
	}

	//implements ToolEffectIfc
	public void spend(int amount)
	{
		preview.spend(amount);
	}

    public void setTool(MicropolisTool tool) {
        preview.setTool(tool);
    }

    public MicropolisTool getTool() {
        return preview.getTool();
    }

	//implements ToolEffectIfc
	public void toolResult(ToolResult tr)
	{
		preview.toolResult(tr);
	}

	ToolResult apply()
	{
		if (originX - preview.offsetX < 0 ||
			originX - preview.offsetX + preview.getWidth() > city.getWidth() ||
			originY - preview.offsetY < 0 ||
			originY - preview.offsetY + preview.getHeight() > city.getHeight())
		{
			return ToolResult.UH_OH;
		}

		if (city.budget.totalFunds < preview.cost) {
			return ToolResult.INSUFFICIENT_FUNDS;
		}


        // testing if tool need a certain BuildingTechnology to be applied
        // iterate tech list if building is inside one of the building technos
        for(BuildingTechnology t : city.buildingTechs){
            if(t.getTool() == preview.getTool()){
                if(t.getIsResearched() == false) return ToolResult.RESEARCH_REQUIRED ; //fix deny message
            }
        }

        if (city.getCityPopulation() < preview.getTool().getMinPopulation()) {
            return ToolResult.INSUFFICIENT_POPULATION;
        }

        if (preview.getTool() == MicropolisTool.CITYHALL) {

            if (city.cityhallCountMem >= city.evaluation.cityClass)
                return ToolResult.INSUFFICIENT_POPULATION;
            if (city.cityhallCountMem >= 5)
                return ToolResult.NO_MORE_CITYHALLS;

            // stupid hacks because the map scanner is not fast enough
            city.cityhallCount++;
            city.lastCityHallCount++;
            city.cityhallCountMem++;
        }

        if ((preview.getTool() == MicropolisTool.UNIA || preview.getTool() == MicropolisTool.UNIB) && city.lastSchoolCount < 1)
            return ToolResult.NEED_A_SCHOOL;


		boolean anyFound = false;
		for (int y = 0; y < preview.tiles.length; y++) {
			for (int x = 0; x < preview.tiles[y].length; x++) {
				int c = preview.tiles[y][x];
				if (c != CLEAR) {
					city.setTile(originX + x - preview.offsetX, originY + y - preview.offsetY, (char) c);
					anyFound = true;
				}
			}
		}

		for (ToolPreview.SoundInfo si : preview.sounds)
		{
			city.makeSound(si.x, si.y, si.sound);
		}

        if (anyFound && preview.getTool().getToolCost() != 0) {
            city.spend(preview.cost);
            return ToolResult.SUCCESS;
		}
		else {
			return preview.toolResult;
		}
	}
}
