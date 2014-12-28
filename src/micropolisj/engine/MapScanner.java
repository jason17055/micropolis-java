// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

import java.util.*;

import static micropolisj.engine.TileConstants.*;
import static micropolisj.engine.TrafficGen.ZoneType;
import static micropolisj.engine.TrafficGen2.*;

/**
 * Process individual tiles of the map for each cycle.
 * In each sim cycle each tile will get activated, and this
 * class contains the activation code.
 */
class MapScanner extends TileBehavior
{
	final B behavior;

	MapScanner(Micropolis city, B behavior)
	{
		super(city);
		this.behavior = behavior;
	}

	public static enum B
	{
		RESIDENTIAL,
		HOSPITAL_CHURCH,
		COMMERCIAL,
		INDUSTRIAL,
		COAL,
		NUCLEAR,
		FIRESTATION,
		POLICESTATION,
		STADIUM_EMPTY,
		STADIUM_FULL,
		AIRPORT,
		SEAPORT;
	}

	@Override
	public void apply()
	{
		switch (behavior) {
		case RESIDENTIAL:
			doResidential();
			return;
		case HOSPITAL_CHURCH:
			doHospitalChurch();
			return;
		case COMMERCIAL:
			doCommercial();
			return;
		case INDUSTRIAL:
			doIndustrial();
			return;
		case COAL:
			doCoalPower();
			return;
		case NUCLEAR:
			doNuclearPower();
			return;
		case FIRESTATION:
			doFireStation();
			return;
		case POLICESTATION:
			doPoliceStation();
			return;
		case STADIUM_EMPTY:
			doStadiumEmpty();
			return;
		case STADIUM_FULL:
			doStadiumFull();
			return;
		case AIRPORT:
			doAirport();
			return;
		case SEAPORT:
			doSeaport();
			return;
		default:
			assert false;
		}
	}

	boolean checkZonePower()
	{
		boolean zonePwrFlag = setZonePower();

		if (zonePwrFlag)
		{
			city.poweredZoneCount++;
		}
		else
		{
			city.unpoweredZoneCount++;
		}

		return zonePwrFlag;
	}

	boolean setZonePower()
	{
		boolean oldPower = city.isTilePowered(xpos, ypos);
		boolean newPower = (
			tile == NUCLEAR ||
			tile == POWERPLANT ||
			city.hasPower(xpos,ypos)
			);

		if (newPower && !oldPower)
		{
			city.setTilePower(xpos, ypos, true);
			city.powerZone(xpos, ypos, getZoneSizeFor(tile));
		}
		else if (!newPower && oldPower)
		{
			city.setTilePower(xpos, ypos, false);
			city.shutdownZone(xpos, ypos, getZoneSizeFor(tile));
		}

		return newPower;
	}

	/**
	 * Place a 3x3 zone on to the map, centered on the current location.
	 * Note: nothing is done if part of this zone is off the edge
	 * of the map or is being flooded or radioactive.
	 *
	 * @param base The "zone" tile value for this zone.
	 * @return true iff the zone was actually placed.
	 */
	boolean zonePlop(TileSpec base)
	{
		assert base.zone;

		TileSpec.BuildingInfo bi = base.getBuildingInfo();
		assert bi != null;
		if (bi == null)
			return false;

		int xorg = xpos-1;
		int yorg = ypos-1;

		for (int y = yorg; y < yorg+bi.height; y++)
		{
			for (int x = xorg; x < xorg+bi.width; x++)
			{
				if (!city.testBounds(x, y)) {
					return false;
				}
				if (isIndestructible(city.getTile(x,y))) {
					// radioactive, on fire, or flooded
					return false;
				}
			}
		}

		assert bi.members.length == bi.width * bi.height;
		int i = 0;
		for (int y = yorg; y < yorg+bi.height; y++)
		{
			for (int x = xorg; x < xorg+bi.width; x++)
			{
				city.setTile(x, y, (char) bi.members[i].tileNumber);
				i++;
			}
		}

		// refresh own tile property
		this.tile = city.getTile(xpos, ypos);

		setZonePower();
		return true;
	}

	void doCoalPower()
	{
		boolean powerOn = checkZonePower();
		city.coalCount++;
		if ((city.cityTime % 8) == 0) {
			repairZone(POWERPLANT);
		}

		city.powerPlants.add(new CityLocation(xpos,ypos));
	}

	void doNuclearPower()
	{
		boolean powerOn = checkZonePower();
		if (!city.noDisasters && PRNG.nextInt(city.MltdwnTab[city.gameLevel]+1) == 0) {
			city.doMeltdown(xpos, ypos);
			return;
		}

		city.nuclearCount++;
		if ((city.cityTime % 8) == 0) {
			repairZone(NUCLEAR);
		}

		city.powerPlants.add(new CityLocation(xpos, ypos));
	}

	void doFireStation()
	{
		boolean powerOn = checkZonePower();
		city.fireStationCount++;
		if ((city.cityTime % 8) == 0) {
			repairZone(FIRESTATION);
		}

		int z;
		if (powerOn) {
			z = city.fireEffect;  //if powered, get effect
		} else {
			z = city.fireEffect/2; // from the funding ratio
		}

		TrafficGen traffic = new TrafficGen(city);
		traffic.mapX = xpos;
		traffic.mapY = ypos;
		if (!traffic.findPerimeterRoad()) {
			z /= 2;
		}

		city.fireStMap[ypos/8][xpos/8] += z;
	}

	void doPoliceStation()
	{
		boolean powerOn = checkZonePower();
		city.policeCount++;
		if ((city.cityTime % 8) == 0) {
			repairZone(POLICESTATION);
		}

		int z;
		if (powerOn) {
			z = city.policeEffect;
		} else {
			z = city.policeEffect / 2;
		}

		TrafficGen traffic = new TrafficGen(city);
		traffic.mapX = xpos;
		traffic.mapY = ypos;
		if (!traffic.findPerimeterRoad()) {
			z /= 2;
		}

		city.policeMap[ypos/8][xpos/8] += z;
	}

	void doStadiumEmpty()
	{
		boolean powerOn = checkZonePower();
		city.stadiumCount++;
		if ((city.cityTime % 16) == 0) {
			repairZone(STADIUM);
		}

		if (powerOn)
		{
			if (((city.cityTime + xpos + ypos) % 32) == 0) {
				drawStadium(FULLSTADIUM);
				city.setTile(xpos+1,ypos, (char)(FOOTBALLGAME1));
				city.setTile(xpos+1,ypos+1,(char)(FOOTBALLGAME2));
			}
		}
	}

	void doStadiumFull()
	{
		boolean powerOn = checkZonePower();
		city.stadiumCount++;
		if (((city.cityTime + xpos + ypos) % 8) == 0) {
			drawStadium(STADIUM);
		}
	}

	void doAirport()
	{
		boolean powerOn = checkZonePower();
		city.airportCount++;
		if ((city.cityTime % 8) == 0) {
			repairZone(AIRPORT);
		}

		if (powerOn) {

			if (PRNG.nextInt(6) == 0) {
				city.generatePlane(xpos, ypos);
			}

			if (PRNG.nextInt(13) == 0) {
				city.generateCopter(xpos, ypos);
			}
		}
	}

	void doSeaport()
	{
		boolean powerOn = checkZonePower();
		city.seaportCount++;
		if ((city.cityTime % 16) == 0) {
			repairZone(PORT);
		}

		if (powerOn && !city.hasSprite(SpriteKind.SHI)) {
			city.generateShip();
		}
	}

	/**
	 * Place hospital or church if needed.
	 */
	void makeHospital()
	{
		if (city.needHospital > 0)
		{
			zonePlop(Tiles.loadByOrdinal(HOSPITAL));
			city.needHospital = 0;
		}

//FIXME- should be 'else if'
		if (city.needChurch > 0)
		{
			zonePlop(Tiles.loadByOrdinal(CHURCH));
			city.needChurch = 0;
		}
	}

	/**
	 * Called when the current tile is the key tile of a
	 * hospital or church.
	 */
	void doHospitalChurch()
	{
		boolean powerOn = checkZonePower();
		if (tile == HOSPITAL)
		{
			city.hospitalCount++;

			if (city.cityTime % 16 == 0)
			{
				repairZone(HOSPITAL);
			}
			if (city.needHospital == -1)  //too many hospitals
			{
				if (PRNG.nextInt(21) == 0)
				{
					zonePlop(Tiles.loadByOrdinal(RESCLR));
				}
			}
		}
		else if (tile == CHURCH)
		{
			city.churchCount++;

			if (city.cityTime % 16 == 0)
			{
				repairZone(CHURCH);
			}
			if (city.needChurch == -1) //too many churches
			{
				if (PRNG.nextInt(21) == 0)
				{
					zonePlop(Tiles.loadByOrdinal(RESCLR));
				}
			}
		}
	}

	/**
	 * Regenerate the tiles that make up a building zone,
	 * repairing from fire, etc.
	 * Only tiles that are not rubble, radioactive, flooded, or
	 * on fire will be regenerated.
	 *
	 * @param base The "zone" tile spec for this zone.
	 */
	void repairZone(int base)
	{
		assert isZoneCenter(base);

		boolean powerOn = city.isTilePowered(xpos, ypos);

		TileSpec.BuildingInfo bi = Tiles.get(base).getBuildingInfo();
		assert bi != null;

		int xorg = xpos-1;
		int yorg = ypos-1;

		assert bi.members.length == bi.width * bi.height;
		int i = 0;
		for (int y = 0; y < bi.height; y++)
		{
			for (int x = 0; x < bi.width; x++, i++)
			{
				int xx = xorg + x;
				int yy = yorg + y;

				TileSpec ts = bi.members[i];
				if (powerOn && ts.onPower != null) {
					ts = ts.onPower;
				}

				if (city.testBounds(xx, yy))
				{
					int thCh = city.getTile(xx, yy);
					if (isZoneCenter(thCh)) {
						continue;
					}

					if (isAnimated(thCh))
						continue;

					if (isRubble(thCh))
						continue;

					if (!isIndestructible(thCh))
					{  //not radiactive, on fire or flooded

						city.setTile(xx,yy,(char) ts.tileNumber);
					}
				}
			}
		}
	}

	/**
	 * Called when the current tile is the key tile of a commercial
	 * zone.
	 */
	void doCommercial()
	{
		dispenseOutput(Commodity.SERVICE);

		boolean powerOn = checkZonePower();
		city.comZoneCount++;

		int tpop = commercialZonePop(tile);
		city.comPop += tpop;

		if (tpop != 0) {
			int supplies = city.getCommodityQuantity(xpos, ypos, Commodity.GOODS);
			int labor = city.getCommodityQuantity(xpos, ypos, Commodity.LABOR);
			int curOutput = city.getCommodityQuantity(xpos, ypos, Commodity.SERVICE);
			int maxOutput = tpop*8*3;

			int workDone = Math.min(tpop*8, Math.min(supplies, labor));
			workDone = Math.min(workDone, (maxOutput-curOutput)/2);
			if (workDone != 0) {
				city.subtractCommodity(xpos, ypos, Commodity.LABOR, workDone);
				city.subtractCommodity(xpos, ypos, Commodity.GOODS, workDone);
				city.addCommodity(xpos, ypos, Commodity.SERVICE, workDone*2);
			}
		}

		int trafficModifier;
		if (isTrafficCycle())
		{
			int earnings = makeTraffic(ZoneType.COMMERCIAL, tpop*8);
			trafficModifier = earnings * 60;
		}
		else
		{
			int earnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);
			trafficModifier = earnings * 60;
		}

		if (PRNG.nextInt(8) == 0)
		{
			int locValve = evalCommercial(trafficModifier);
			int zscore = city.comValve + locValve;

			if (!powerOn)
				zscore = -500;

			if (zscore > -350 &&
				zscore - 26380 > (PRNG.nextInt(0x10000)-0x8000))
			{
				int value = getCRValue();
				doCommercialIn(tpop, value);
				return;
			}

			if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000)-0x8000))
			{
				int value = getCRValue();
				doCommercialOut(tpop, value);
				return;
			}
		}
	}

	/**
	 * Called when the current tile is the key tile of an
	 * industrial zone.
	 */
	void doIndustrial()
	{
		dispenseOutput(Commodity.GOODS);

		boolean powerOn = checkZonePower();
		city.indZoneCount++;

		int tpop = industrialZonePop(tile);
		city.indPop += tpop;

		if (tpop != 0) {
			int labor = city.getCommodityQuantity(xpos, ypos, Commodity.LABOR);
			int curOutput = city.getCommodityQuantity(xpos, ypos, Commodity.GOODS);
			int maxOutput = tpop*8*6;

			int workDone = Math.min(tpop*8, labor);
			workDone = Math.min(workDone, maxOutput-curOutput);
			if (workDone != 0) {
				city.subtractCommodity(xpos, ypos, Commodity.LABOR, workDone);
				city.addCommodity(xpos, ypos, Commodity.GOODS, workDone);
			}
		}

		int trafficModifier;
		if (isTrafficCycle())
		{
			int earnings = makeTraffic(ZoneType.INDUSTRIAL, tpop*8);
			trafficModifier = earnings * 60;
		}
		else
		{
			int earnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);
			trafficModifier = earnings * 60;
		}

		if (PRNG.nextInt(8) == 0)
		{
			int locValve = evalIndustrial(trafficModifier);
			int zscore = city.indValve + locValve;

			if (!powerOn)
				zscore = -500;

			if (zscore > -350 &&
				zscore - 26380 > (PRNG.nextInt(0x10000)-0x8000))
			{
				int value = PRNG.nextInt(2);
				doIndustrialIn(tpop, value);
				return;
			}

			if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000)-0x8000))
			{
				int value = PRNG.nextInt(2);
				doIndustrialOut(tpop, value);
				return;
			}
		}
	}

	/**
	 * Called when the current tile is the key tile of a
	 * residential zone.
	 */
	void doResidential()
	{
		dispenseOutput(Commodity.LABOR);

		boolean powerOn = checkZonePower();
		city.resZoneCount++;

		int tpop; //population of this zone
		if (tile == RESCLR)
		{
			tpop = city.doFreePop(xpos, ypos);
		}
		else
		{
			tpop = residentialZonePop(tile);
		}

		if (tpop != 0) {
			int food = city.getCommodityQuantity(xpos, ypos, Commodity.SERVICE);
			int curOutput = city.getCommodityQuantity(xpos, ypos, Commodity.LABOR);
			int maxOutput = tpop*3;

			int workDone = Math.min(tpop, food);
			if (workDone != 0) {
				city.subtractCommodity(xpos, ypos, Commodity.SERVICE, workDone);
			}

			workDone += tpop;
			workDone = Math.min(workDone, maxOutput-curOutput);
			if (workDone != 0) {
				city.addCommodity(xpos, ypos, Commodity.LABOR, workDone);
			}
		}

		city.resPop += tpop;

		int trafficModifier;
		if (isTrafficCycle())
		{
			int earnings = makeTraffic(ZoneType.RESIDENTIAL, tpop);
			trafficModifier = earnings * 60;
		}
		else
		{
			int earnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);
			trafficModifier = earnings * 60;
		}

		if (tile == RESCLR || PRNG.nextInt(8) == 0)
		{
			int locValve = evalResidential(trafficModifier);
			int zscore = city.resValve + locValve;

			if (!powerOn)
				zscore = -500;

			if (zscore > -350 && zscore - 26380 > (PRNG.nextInt(0x10000)-0x8000))
			{
				if (tpop == 0 && PRNG.nextInt(4) == 0)
				{
					makeHospital();
					return;
				}

				int value = getCRValue();
				doResidentialIn(tpop, value);
				return;
			}

			if (zscore < 350 && zscore + 26380 < (PRNG.nextInt(0x10000)-0x8000))
			{
				int value = getCRValue();
				doResidentialOut(tpop, value);
				return;
			}
		}
	}

	void shuffleArray(Traffic [] A)
	{
		for (int i = 0; i < A.length; i++) {
			int j = PRNG.nextInt(A.length-i) + i;
			Traffic tmp = A[i];
			A[i] = A[j];
			A[j] = tmp;
		}
	}

	void dispenseOutput(Commodity type)
	{
		CityLocation loc = new CityLocation(xpos, ypos);
		int count = city.getCommodityQuantity(xpos, ypos, type);
		int origCount = count;

		Traffic [] trafConnections = city.enumIncomingConnections(loc, type)
				.toArray(new Traffic[0]);
		shuffleArray(trafConnections);

		int totalDemand = 0;
		for (Traffic traf : trafConnections)
		{
			totalDemand += traf.demand;
			int amt = Math.min(traf.demand, count);
			city.adjustTrafficLevelTo(traf, amt);

			if (amt != 0) {
				count -= amt;
				city.addCommodity(traf.from.x, traf.from.y, type, amt);
			}
		}

		if (count != origCount) {
			city.subtractCommodity(xpos, ypos, type, origCount-count);
		}

		adjustPrices(type, origCount, totalDemand);
	}

	int countIncomingTraffic(Commodity type)
	{
		int sum = 0;
		for (Traffic c : city.enumIncomingConnections(
						new CityLocation(xpos, ypos),
						type))
		{
			sum += c.count;
		}
		return sum;
	}

	void adjustPrices(Commodity type, int supply, int demand)
	{
		int price = city.getPrice(xpos, ypos, type, 0);
		int adj = demand-supply;
		int newPrice = Math.max(1, price+adj);
		if (newPrice != price) {
			city.setPrice(xpos, ypos, type, newPrice);
		}
	}

	/** Indicates how many residences each commercial zone supplies. */
	static final int COM_FACTOR = 2;

	/**
	 * Consider the value of building a single-lot house at certain
	 * coordinates.
	 * @return integer; positive number indicates good place for
	 * house to go; zero or a negative number indicates a bad place.
	 */
	int evalLot(int x, int y)
	{
		// test for clear lot
		int aTile = city.getTile(x,y);
		if (aTile != DIRT && !isResidentialClear(aTile)) {
			return -1;
		}

		int score = 1;

		final int [] DX = { 0, 1, 0, -1 };
		final int [] DY = { -1, 0, 1, 0 };
		for (int z = 0; z < 4; z++)
		{
			int xx = x + DX[z];
			int yy = y + DY[z];

			// look for road
			if (city.testBounds(xx, yy)) {
				int tmp = city.getTile(xx, yy);
				if (isRoad(tmp) || isRail(tmp))
				{
					score++;
				}
			}
		}

		return score;
	}

	/**
	 * Build a single-lot house on the current residential zone.
	 */
	private void buildHouse(int value)
	{
		assert value >= 0 && value <= 3;

		final int [] ZeX = { 0, -1, 0, 1, -1, 1, -1, 0, 1 };
		final int [] ZeY = { 0, -1, -1, -1, 0, 0, 1, 1, 1 };

		int bestLoc = 0;
		int hscore = 0;

		for (int z = 1; z < 9; z++)
		{
			int xx = xpos + ZeX[z];
			int yy = ypos + ZeY[z];

			if (city.testBounds(xx, yy))
			{
				int score = evalLot(xx, yy);

				if (score != 0)
				{
					if (score > hscore)
					{
						hscore = score;
						bestLoc = z;
					}

					if ((score == hscore) && PRNG.nextInt(8) == 0)
					{
						bestLoc = z;
					}
				}
			}
		}

		if (bestLoc != 0)
		{
			int xx = xpos + ZeX[bestLoc];
			int yy = ypos + ZeY[bestLoc];
			int houseNumber = value * 3 + PRNG.nextInt(3);
			assert houseNumber >= 0 && houseNumber < 12;

			assert city.testBounds(xx, yy);
			city.setTile(xx, yy, (char)(HOUSE + houseNumber));
		}
	}

	private void doCommercialIn(int pop, int value)
	{
		int z = city.getLandValue(xpos, ypos) / 32;
		if (pop > z)
			return;

		if (pop < 5)
		{
			comPlop(pop, value);
			adjustROG(8);
		}
	}

	private void doIndustrialIn(int pop, int value)
	{
		if (pop < 4)
		{
			indPlop(pop, value);
			adjustROG(8);
		}
	}

	private void doResidentialIn(int pop, int value)
	{
		assert value >= 0 && value <= 3;

		int z = city.pollutionMem[ypos/2][xpos/2];
		if (z > 128)
			return;

		if (tile == RESCLR)
		{
			if (pop < 8)
			{
				buildHouse(value);
				adjustROG(1);
				return;
			}

			if (city.getPopulationDensity(xpos, ypos) > 64)
			{
				residentialPlop(0, value);
				adjustROG(8);
				return;
			}
			return;
		}

		if (pop < 40)
		{
			residentialPlop(pop / 8 - 1, value);
			adjustROG(8);
		}
	}

	void comPlop(int density, int value)
	{
		int oldPrice = city.getPrice(xpos, ypos, Commodity.SERVICE, 0);
		int oldEarnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);

		int base = (value * 5 + density) * 9 + CZB;
		zonePlop(Tiles.loadByOrdinal(base));

		city.setPrice(xpos, ypos, Commodity.SERVICE, oldPrice);
		city.setTileExtra(xpos, ypos, "earnings", Integer.toString(oldEarnings));
	}

	void indPlop(int density, int value)
	{
		int oldPrice = city.getPrice(xpos, ypos, Commodity.GOODS, 0);
		int oldEarnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);

		int base = (value * 4 + density) * 9 + IZB;
		zonePlop(Tiles.loadByOrdinal(base));

		city.setPrice(xpos, ypos, Commodity.GOODS, oldPrice);
		city.setTileExtra(xpos, ypos, "earnings", Integer.toString(oldEarnings));
	}

	void residentialPlop(int density, int value)
	{
		int oldPrice = city.getPrice(xpos, ypos, Commodity.LABOR, 0);
		int oldEarnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);

		int base = (value * 4 + density) * 9 + RZB;
		zonePlop(Tiles.loadByOrdinal(base));

		city.setPrice(xpos, ypos, Commodity.LABOR, oldPrice);
		city.setTileExtra(xpos, ypos, "earnings", Integer.toString(oldEarnings));
	}

	private void doCommercialOut(int pop, int value)
	{
		if (pop > 1)
		{
			comPlop(pop-2, value);
			adjustROG(-8);
		}
		else if (pop == 1)
		{
			zonePlop(Tiles.loadByOrdinal(COMCLR));
			adjustROG(-8);
		}
	}

	private void doIndustrialOut(int pop, int value)
	{
		if (pop > 1)
		{
			indPlop(pop-2, value);
			adjustROG(-8);
		}
		else if (pop == 1)
		{
			zonePlop(Tiles.loadByOrdinal(INDCLR));
			adjustROG(-8);
		}
	}

	private void doResidentialOut(int pop, int value)
	{
		assert value >= 0 && value < 4;

		final char [] Brdr = { 0, 3, 6, 1, 4, 7, 2, 5, 8 };

		if (pop == 0)
			return;

		if (pop > 16)
		{
			// downgrade to a lower-density full-size residential zone
			residentialPlop((pop-24) / 8, value);
			adjustROG(-8);
			return;
		}

		if (pop == 16)
		{
			// downgrade from full-size zone to 8 little houses

			boolean pwr = city.isTilePowered(xpos, ypos);
			city.setTile(xpos, ypos, RESCLR);
			city.setTilePower(xpos, ypos, pwr);

			for (int x = xpos-1; x <= xpos+1; x++)
			{
				for (int y = ypos-1; y <= ypos+1; y++)
				{
					if (city.testBounds(x,y))
					{
						if (!(x == xpos && y == ypos))
						{
							// pick a random small house
							int houseNumber = value * 3 + PRNG.nextInt(3);
							city.setTile(x, y, (char) (HOUSE + houseNumber));
						}
					}
				}
			}

			adjustROG(-8);
			return;
		}

		if (pop < 16)
		{
			// remove one little house
			adjustROG(-1);
			int z = 0;

			for (int x = xpos-1; x <= xpos+1; x++)
			{
				for (int y = ypos-1; y <= ypos+1; y++)
				{
					if (city.testBounds(x,y))
					{
						int loc = city.getTile(x, y);
						if (loc >= LHTHR && loc <= HHTHR)
						{ //little house
							city.setTile(x, y, (char)(Brdr[z] + RESCLR - 4));
							return;
						}
					}
					z++;
				}
			}
		}
	}

	/**
	 * Evaluates the zone value of the current commercial zone location.
	 * @return an integer between -3000 and 3000
	 * Same meaning as evalResidential.
	 */
	int evalCommercial(int traf)
	{
		int value = city.comRate[ypos/8][xpos/8];
		value += traf;

		// clamp result between -3000 and 3000
		return Math.min(Math.max(value, -3000), 3000);
	}

	/**
	 * Evaluates the zone value of the current industrial zone location.
	 * @return an integer between -3000 and 3000.
	 * Same meaning as evalResidential.
	 */
	int evalIndustrial(int traf)
	{
		// traffic less important for industry
		return traf / 3;
	}

	/**
	 * Evaluates the zone value of the current residential zone location.
	 * @return an integer between -3000 and 3000. The higher the
	 * number, the more likely the zone is to GROW; the lower the
	 * number, the more likely the zone is to SHRINK.
	 */
	int evalResidential(int traf)
	{
		int value = city.getLandValue(xpos, ypos);
		value -= city.pollutionMem[ypos/2][xpos/2];
		value *= 32;
		value -= 3000;

		value += traf;

		// clamp result between -3000 and 3000
		return Math.min(Math.max(value, -3000), 3000);
	}

	/**
	 * Gets the land-value class (0-3) for the current
	 * residential or commercial zone location.
	 * @return integer from 0 to 3, 0 is the lowest-valued
	 * zone, and 3 is the highest-valued zone.
	 */
	int getCRValue()
	{
		int lval = city.getLandValue(xpos, ypos);
		lval -= city.pollutionMem[ypos/2][xpos/2];

		if (lval < 30)
			return 0;

		if (lval < 80)
			return 1;

		if (lval < 150)
			return 2;

		return 3;
	}

	/**
	 * Record a zone's population change to the rate-of-growth
	 * map.
	 * An adjustment of +/- 1 corresponds to one little house.
	 * An adjustment of +/- 8 corresponds to a full-size zone.
	 *
	 * @param amount the positive or negative adjustment to record.
	 */
	void adjustROG(int amount)
	{
		city.rateOGMem[ypos/8][xpos/8] += 4*amount;
	}

	/**
	 * Place tiles for a stadium (full or empty).
	 * @param zoneCenter either STADIUM or FULLSTADIUM
	 */
	void drawStadium(int zoneCenter)
	{
		int zoneBase = zoneCenter - 1 - 4;

		for (int y = 0; y < 4; y++)
		{
			for (int x = 0; x < 4; x++)
			{
				city.setTile(xpos - 1 + x, ypos - 1 + y, (char)zoneBase);
				zoneBase++;
			}
		}
		city.setTilePower(xpos, ypos, true);
	}

	boolean hasResources(int x, int y)
	{
		return city.hasPrice(x, y, Commodity.GOODS);
	}

	int getResourcePrice(int x, int y)
	{
		int tile = city.getTile(x, y);
		assert (isZoneCenter(tile) && isIndustrialZone(tile));

		return city.getPrice(x, y, Commodity.GOODS, 0);
	}

	boolean hasLabor(int x, int y)
	{
		return city.hasPrice(x, y, Commodity.LABOR);
	}

	int getLaborPrice(int x, int y)
	{
		int tile = city.getTile(x, y);
		assert (isZoneCenter(tile) && isResidentialZoneAny(tile));

		return city.getPrice(x, y, Commodity.LABOR, 0);
	}

	boolean hasGoods(int x, int y)
	{
		return city.hasPrice(x, y, Commodity.SERVICE);
	}

	int getGoodsPrice(int x, int y)
	{
		int tile = city.getTile(x, y);
		assert (isZoneCenter(tile) && isCommercialZone(tile));

		return city.getPrice(x, y, Commodity.SERVICE, 0);
	}

	/**
	 * @return the revenue less expenses for living in this zone
	 */
	int makeTraffic(ZoneType zoneType, int pop)
	{
		TrafficGen2 traffic = new TrafficGen2(city, new CityLocation(xpos, ypos));
		traffic.prepare();

		int slot = (city.cityTime + xpos + ypos) % JOB_SLOT_COUNT;
		int count = (pop + JOB_SLOT_COUNT - slot - 1) / JOB_SLOT_COUNT;

		int earnings = 50; //base earnings
		switch (zoneType) {
		case RESIDENTIAL:
			earnings += getLaborPrice(xpos, ypos);
			int resPop = city.history.res[0] * 8;
			earnings -= seekJob(traffic, Commodity.SERVICE, slot, count, Math.min(resPop * 3 / 8, MAX_COST));
			break;

		case COMMERCIAL:
			earnings += COM_FACTOR * getGoodsPrice(xpos, ypos);
			earnings -= seekJob(traffic, Commodity.LABOR, slot, count);
			earnings -= seekJob(traffic, Commodity.GOODS, slot, count);
			break;

		case INDUSTRIAL:
			earnings += getResourcePrice(xpos, ypos);
			earnings -= seekJob(traffic, Commodity.LABOR, slot, count);
			break;
		}

		int oldEarnings = city.getTileExtraInt(xpos, ypos, "earnings", 0);
		int newEarnings = (oldEarnings * 3 + earnings) / 4;
		city.setTileExtra(xpos, ypos, "earnings", Integer.toString(newEarnings));
		return newEarnings;
	}

	static final int MAX_COST = 256;

	private int seekJob(TrafficGen2 traffic, Commodity connType, int slot, int count)
	{
		return seekJob(traffic, connType, slot, count, MAX_COST);
	}

	/**
	 * Given a traffic map analysis, generate a route of a particular type,
	 * and add traffic onto the city map.
	 * @param maxCost the maximum destination cost tolerated
	 * @return the COST of the route (or maxCost if no route found)
	 */
	private int seekJob(TrafficGen2 traffic, Commodity connType,
			int slot, int count, int maxCost)
	{
		TrafficGen2.FitnessFunction f;
		switch (connType) {
		case LABOR:
			f = new TrafficGen2.FitnessFunction() {
			public double fitness(int xpos, int ypos, int dist) {
				return hasLabor(xpos, ypos) ?
					Math.exp(-(dist + getLaborPrice(xpos, ypos))) :
					0.0;
			}};
			break;
		case SERVICE:
			f = new TrafficGen2.FitnessFunction() {
			public double fitness(int xpos, int ypos, int dist) {
				return hasGoods(xpos, ypos) ?
					Math.exp(-(dist + getGoodsPrice(xpos, ypos))) :
					hasResources(xpos, ypos) ?
					Math.exp(-(dist + 15 + getResourcePrice(xpos, ypos))) :
					0.0;
			}};
			break;
		case GOODS:
			f = new TrafficGen2.FitnessFunction() {
			public double fitness(int xpos, int ypos, int dist) {
				return hasResources(xpos, ypos) ?
					Math.exp(-(dist + getResourcePrice(xpos, ypos))) :
					0.0;
			}};
			break;
		default:
			throw new Error("unreachable");
		}

		Traffic conn = city.findConnection(
				new CityLocation(xpos, ypos),
				connType,
				slot);
		if (conn != null &&
			conn.count == count &&
			PRNG.nextInt(4) != 0)
		{
			// keep old job, assuming there's still a path to get there
			double x = f.fitness(conn.to.x, conn.to.y, traffic.getDist(conn.to.x, conn.to.y));
			int pathCost = (int)Math.round(Math.min(-Math.log(x), maxCost));
			if (pathCost < maxCost) {
				// keep old job, but still update how to get there
				city.applyTraffic(conn.from, conn.pathTaken, -conn.count);
				conn.pathTaken = traffic.getPathTo(conn.to);
				city.applyTraffic(conn.from, conn.pathTaken, conn.count);
				return pathCost;
			}
		}

		if (conn != null) {
			// remove old connection
			city.removeConnection(conn);
		}

		CityLocation employer = traffic.findBest(f);
		if (employer != null && count != 0) {
			double x = f.fitness(employer.x, employer.y, traffic.getDist(employer.x, employer.y));
			int pathCost = (int)Math.round(Math.min(-Math.log(x), maxCost));
			if (pathCost < maxCost) {
				setJob(connType, slot, count, employer, traffic.getPathTo(employer));
				return pathCost;
			}
		}

		// no supply found, set cost to ~infinite
		return maxCost;
	}

	static final int JOB_SLOT_COUNT = 3;
	static final int TRAFFIC_CYCLE = 11;

	void setJob(Commodity connType, int slot, int count, CityLocation dest, int [] pathTaken)
	{
		if (count != 0 && dest != null && pathTaken != null) {

			Traffic conn = new Traffic(
					new CityLocation(xpos, ypos),
					dest);
			conn.type = connType;
			conn.slot = slot;
			conn.count = count;
			conn.demand = count;
			conn.pathTaken = pathTaken;

			city.addConnection(conn);
		}
	}

	boolean isTrafficCycle()
	{
		return (city.cityTime + xpos + ypos) % TRAFFIC_CYCLE == 0;
	}
}
