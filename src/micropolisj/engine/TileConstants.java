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

//import java.util.Arrays;

/**
 * Contains symbolic names of certain tile values,
 * and helper functions to test tile attributes.
 * Attributes of tiles that are interesting:
 * <ul>
 * <li>ZONE - the special tile for a zone
 * <li>ANIM - the tile animates
 * <li>BULL - is bulldozable
 * <li>BURN - is combustible
 * <li>COND - can conduct power
 * <li>Road - traffic
 * <li>Rail - railroad
 * <li>Floodable - subject to floods
 * <li>Wet
 * <li>Rubble
 * <li>Tree
 * <li>OverWater
 * <li>Arsonable
 * <li>Vulnerable - vulnerable to earthquakes
 * <li>Bridge
 * <li>AutoDozeRRW - automatically bulldoze when placing Road/Rail/Wire
 * <li>AutoDozeZ - automatically bulldoze when placing Zone
 * </ul>
 */
public class TileConstants
{
	//
	// terrain mapping
	//
	public static final short CLEAR = -1;
	public static final char DIRT = 0;
	static final char RIVER = 2;
	static final char REDGE = 3;
	static final char CHANNEL = 4;
	static final char RIVEDGE = 5;
	static final char FIRSTRIVEDGE = 5;
	static final char LASTRIVEDGE = 20;
	static final char TREEBASE = 21;
	static final char WOODS_LOW = TREEBASE;
	static final char WOODS = 37;
	static final char WOODS_HIGH = 39;
	static final char WOODS2 = 40;
	static final char WOODS5 = 43;
	static final char RUBBLE = 44;
	static final char LASTRUBBLE = 47;
	static final char FLOOD = 48;
	static final char LASTFLOOD = 51;
	static final char RADTILE = 52;
	static final char FIRE = 56;
	static final char ROADBASE = 64;
	static final char HBRIDGE = 64;
	static final char VBRIDGE = 65;
	static final char ROADS = 66;
	static final char ROADS2 = 67;
	private static final char ROADS3 = 68;
	private static final char ROADS4 = 69;
	private static final char ROADS5 = 70;
	private static final char ROADS6 = 71;
	private static final char ROADS7 = 72;
	private static final char ROADS8 = 73;
	private static final char ROADS9 = 74;
	private static final char ROADS10 = 75;
	static final char INTERSECTION = 76;
	static final char HROADPOWER = 77;
	static final char VROADPOWER = 78;
	static final char BRWH = 79;       //horz bridge, open
	static final char LTRFBASE = 80;
	static final char BRWV = 95;       //vert bridge, open
	static final char HTRFBASE = 144;
	private static final char LASTROAD = 206;
	static final char POWERBASE = 208;
	static final char HPOWER = 208;    //underwater power-line
	static final char VPOWER = 209;
	static final char LHPOWER = 210;
	static final char LVPOWER = 211;
	static final char LVPOWER2 = 212;
	private static final char LVPOWER3 = 213;
	private static final char LVPOWER4 = 214;
	private static final char LVPOWER5 = 215;
	private static final char LVPOWER6 = 216;
	private static final char LVPOWER7 = 217;
	private static final char LVPOWER8 = 218;
	private static final char LVPOWER9 = 219;
	private static final char LVPOWER10 = 220;
	static final char RAILHPOWERV = 221;
	static final char RAILVPOWERH = 222;
	static final char LASTPOWER = 222;
	static final char RAILBASE = 224;
	static final char HRAIL = 224;     //underwater rail (horz)
	static final char VRAIL = 225;     //underwater rail (vert)
	static final char LHRAIL = 226;
	static final char LVRAIL = 227;
	static final char LVRAIL2 = 228;
	private static final char LVRAIL3 = 229;
	private static final char LVRAIL4 = 230;
	private static final char LVRAIL5 = 231;
	private static final char LVRAIL6 = 232;
	private static final char LVRAIL7 = 233;
	private static final char LVRAIL8 = 234;
	private static final char LVRAIL9 = 235;
	private static final char LVRAIL10 = 236;
	static final char HRAILROAD = 237;
	static final char VRAILROAD = 238;
	static final char LASTRAIL = 238;
	static final char RESBASE = 240;
	static final char RESCLR = 244;
	static final char HOUSE = 249;
	static final char LHTHR = 249;  //12 house tiles
	static final char HHTHR = 260;
	static final char RZB = 265; //residential zone base
	static final char HOSPITAL = 409;
	static final char CHURCH = 418;
	static final char COMBASE = 423;
	static final char COMCLR = 427;
	static final char CZB = 436; //commercial zone base
	static final char INDBASE = 612;
	static final char INDCLR = 616;
	static final char IZB = 625;
	static final char PORTBASE = 693;
	static final char PORT = 698;
	static final char AIRPORT = 716;
	static final char POWERPLANT = 750;
	static final char FIRESTATION = 765;
	static final char POLICESTATION = 774;
	static final char STADIUM = 784;
	static final char FULLSTADIUM = 800;
	static final char NUCLEAR = 816;
	static final char LASTZONE = 826;
	public static final char LIGHTNINGBOLT = 827;
	static final char HBRDG0 = 828;   //draw bridge tiles (horz)
	static final char HBRDG1 = 829;
	static final char HBRDG2 = 830;
	static final char HBRDG3 = 831;
	static final char FOUNTAIN = 840;
	static final char TINYEXP = 860;
	private static final char LASTTINYEXP = 867;
	static final char FOOTBALLGAME1 = 932;
	static final char FOOTBALLGAME2 = 940;
	static final char VBRDG0 = 948;   //draw bridge tiles (vert)
	static final char VBRDG1 = 949;
	static final char VBRDG2 = 950;
	static final char VBRDG3 = 951;
	static final char NEWZONE = 963;
	static final char SCHOOLBUILDING = 964;
	static final char MUSEUMBUILDING = 973;
	static final char UNIABUILDING = 982;
	static final char UNIBBUILDING = 991;
	static final char CITYHALLBUILDING = 1000;
	static final char OPENAIRBUILDING = 1012;
	static final char BIGPARKBUILDING = 1045;
	static final char SOLAR = 1055;
	static final char WIND = 1066;
	public static final char LAST_TILE = 1066;
	static final char NEWLASTZONE = 1067;
	static final char BIGROADBASE = 1067;
	static final char BIGHBRIDGE = 1067;
	static final char BIGVBRIDGE = 1068;
	static final char BIGROADS = 1069;
	static final char BIGROADS2 = 1070;
	private static final char BIGROADS3 = 1071;
	private static final char BIGROADS4 = 1072;
	private static final char BIGROADS5 = 1073;
	private static final char BIGROADS6 = 1074;
	private static final char BIGROADS7 = 1075;
	private static final char BIGROADS8 = 1076;
	private static final char BIGROADS9 = 1077;
	private static final char BIGROADS10 = 1078;
	static final char BIGINTERSECTION = 1079;
	static final char BIGHROADPOWER = 1080;
	static final char BIGVROADPOWER = 1081;
	static final char BIGBRWH = 1082;       //horz bridge, open
	static final char BIGLTRFBASE = 1083;
	static final char BIGBRWV = 1098;       //vert bridge, open
	static final char BIGHTRFBASE = 1147;
	private static final char BIGLASTROAD = 1209;
	static final char NARROWINGS   = 1221;
	static final char NARROWINGS2  = 1222;
	static final char NARROWINGS3  = 1223;
	static final char NARROWINGS4  = 1224;
	static final char NARROWINGS5  = 1225;
	static final char NARROWINGS6  = 1226;
	static final char NARROWINGS7  = 1227;  
	static final char NARROWINGS8  = 1228;
	static final char NARROWINGS9  = 1229; 
	static final char NARROWINGS10 = 1230;
	static final char STATION      = 1234;
	static final char STATION2     = 1235;
	static final char STATION3     = 1236;
	static final char STATION4     = 1237;
	static final char STATION5     = 1238;
	static final char STATION6     = 1239;
	static final char STATION7     = 1240;
	static final char STATION8     = 1241;
	static final char STATION9     = 1242;
	static final char STATION10    = 1243;
	static final char STATIONINTERSECTION = 1244;
	static final char BIGHIGH = 1147;
	static final char BIGLIGHT = 1083;

	static final char [] RoadTable = new char[] {
		ROADS, ROADS2, ROADS, ROADS3,
		ROADS2, ROADS2, ROADS4, ROADS8,
		ROADS, ROADS6, ROADS, ROADS7,
		ROADS5, ROADS10, ROADS9, INTERSECTION,
		NARROWINGS, NARROWINGS4, NARROWINGS2, BIGROADS3,
		NARROWINGS3, BIGROADS2, BIGROADS4, BIGROADS8,
		NARROWINGS, BIGROADS6, BIGROADS, BIGROADS7,
		BIGROADS5, BIGROADS10, BIGROADS9, BIGINTERSECTION,
		
		
		};
	
	static final char [] BigRoadTable = new char[] {
		BIGROADS, BIGROADS2, BIGROADS, BIGROADS3,
		BIGROADS2, BIGROADS2, BIGROADS4, BIGROADS8,
		BIGROADS, BIGROADS6, BIGROADS, BIGROADS7,
		BIGROADS5, BIGROADS10, BIGROADS9, BIGINTERSECTION,
		NARROWINGS, NARROWINGS3, NARROWINGS, ROADS3,
		NARROWINGS4, ROADS2, ROADS4, ROADS8,
		NARROWINGS2, ROADS6, ROADS, ROADS7,
		ROADS5, ROADS10, ROADS9, INTERSECTION,
    	};
	
	
	
	static final char [] RailTable = new char[] {
		LHRAIL, LVRAIL, LHRAIL, LVRAIL2,
		LVRAIL, LVRAIL, LVRAIL3, LVRAIL7,
		LHRAIL, LVRAIL5, LHRAIL, LVRAIL6,
		LVRAIL4, LVRAIL9, LVRAIL8, LVRAIL10,
		STATION, STATION2, STATION, STATION3,
		STATION2, STATION2, STATION4, STATION8,
		STATION, STATION6, STATION, STATION7,
		STATION5, STATION10, STATION9, STATIONINTERSECTION
		};
	
	

	static final char [] WireTable = new char[] {
		LHPOWER, LVPOWER, LHPOWER, LVPOWER2,
		LVPOWER, LVPOWER, LVPOWER3, LVPOWER7,
		LHPOWER, LVPOWER5, LHPOWER, LVPOWER6,
		LVPOWER4, LVPOWER9, LVPOWER8, LVPOWER10
		};

	//
	// status bits
	//
	public static final char PWRBIT = 32768;  // bit 15 ... currently powered
	// bit 14 ... unused
	// bit 13 ... unused
	// bit 12 ... unused
	// bit 11 ... unused
	// bit 10 ... unused

	public static final char ALLBITS = 64512;   // mask for upper 6 bits
	public static final char LOMASK = 2047; //mask for low 10 bits

	private TileConstants() {}

	/**
	 * Checks whether the tile can be auto-bulldozed for
	 * placement of road, rail, or wire.
	 */
	public static boolean canAutoBulldozeRRW(int tileValue)
	{
		// can we autobulldoze this tile?
		return (
		(tileValue >= FIRSTRIVEDGE && tileValue <= LASTRUBBLE) ||
		(tileValue >= TINYEXP && tileValue <= LASTTINYEXP)
		);
	}

	/**
	 * Checks whether the tile can be auto-bulldozed for
	 * placement of a zone.
	 */
	public static boolean canAutoBulldozeZ(char tileValue)
	{
		//FIXME- what is significance of POWERBASE+2 and POWERBASE+12 ?

		// can we autobulldoze this tile?
		if ((tileValue >= FIRSTRIVEDGE && tileValue <= LASTRUBBLE) ||
			(tileValue >= POWERBASE + 2 && tileValue <= POWERBASE + 12) ||
			(tileValue >= TINYEXP && tileValue <= LASTTINYEXP))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//used by scanTile
	public static String getTileBehavior(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts != null ? ts.getAttribute("behavior") : null;
	}

	//used by queryZoneStatus
	public static int getDescriptionNumber(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			return ts.getDescriptionNumber();
		}
		else {
			return -1;
		}
	}

	public static int getPollutionValue(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null ? spec.getPollutionValue() : 0;
	}

	public static boolean isAnimated(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.animNext != null;
	}

	//used by setFire()
	public static boolean isArsonable(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (
			!isZoneCenter(tile) &&
			tile >= LHTHR &&
			(tile <= LASTZONE || (tile > NEWZONE && tile <= NEWLASTZONE))
			);
	}

	//used by Sprite::destroyTile
	public static boolean isBridge(int tile)
	{
		return isRoad(tile) && !isCombustible(tile);
	}

	public static boolean isCombustible(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canBurn;
	}

	public static boolean isConductive(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canConduct;
	}

	/** Used in repairZone(). */
	public static boolean isIndestructible(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RUBBLE && tile < ROADBASE;
	}

	/** Used in zonePlop(). */
	public static boolean isIndestructible2(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= FLOOD && tile < ROADBASE;
	}

	public static boolean isOverWater(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.overWater;
	}

	public static boolean isRubble(int tile)
	{
		assert (tile & LOMASK) == tile;

		return ((tile >= RUBBLE) &&
			(tile <= LASTRUBBLE));
	}

	public static boolean isTree(char tile)
	{
		assert (tile & LOMASK) == tile;

		return ((tile >= WOODS_LOW) &&
			(tile <= WOODS_HIGH));
	}

	//used by makeEarthquake
	public static boolean isVulnerable(int tile)
	{
		assert (tile & LOMASK) == tile;

		if (tile < RESBASE ||
			(tile > LASTZONE && tile < NEWZONE) || tile > NEWLASTZONE ||
			isZoneCenter(tile)
			) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkWet(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile == POWERBASE ||
			tile == POWERBASE+1 ||
			tile == RAILBASE ||
			tile == RAILBASE + 1 ||
			tile == BRWH ||
			tile == BRWV);
	}

	public static CityDimension getZoneSizeFor(int tile)
	{
		assert isZoneCenter(tile);
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null ? spec.getBuildingSize() : null;
	}

	public static boolean isConstructed(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= 0 && tile >= ROADBASE;
	}

	static boolean isRiverEdge(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= FIRSTRIVEDGE && tile <= LASTRIVEDGE;
	}

	public static boolean isDozeable(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.canBulldoze;
	}

	static boolean isFloodable(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile == DIRT || (isDozeable(tile) && isCombustible(tile)));
	}

	/**
	 * Note: does not include rail/road tiles.
	 * @see #isRoadAny
	 */
	public static boolean isRoad(int tile) { //TODO add new tiles (new images)
		assert (tile & LOMASK) == tile;

		return ((tile >= ROADBASE && tile < POWERBASE) || tile==237 || tile==238 || (tile >= 1217 && tile <= 1225));
	}
	
	public static boolean isBigRoad(int tile) {
		return ((tile >= 1067 && tile < 1210) || tile == 1215 || tile==1216 || (tile >= 1225 && tile <= 1233));
	}
	public static boolean isStation(int tile) {
		return (tile>=1234 && tile<=1248 && tile!=1244);
	}

	public static boolean isRoadAny(int tile) {
		assert (tile & LOMASK) == tile;

		return isRoad(tile) || isBigRoad(tile) || isRail(tile) || isStation(tile);
	}
	
	/**
	 * checks
	 * @param tile
	 * @return
	 */
	
	public static int roadType(int tile) {
		if (isStation(tile)) {
			return 4;
		} else {
			if (isRoad(tile) && isRail(tile)) {
				return 5;
			} else {
				if (isBigRoad(tile) && isRail(tile)) {
					return 6;
				} else {
					if (isRoad(tile)) {
						return 1;
					} else {
						if (isBigRoad(tile)) {
							return 2;
						} else {
							if (isRail(tile)) {
								return 3;
							}
						}
					}
				}
			}
		}
	return 0;
	}

	/**
	 * Checks whether the tile is a road that will automatically change to connect to
	 * neighboring roads.
	 */
	public static boolean isRoadDynamic(int tile)
	{
		int tmp = neutralizeRoad(tile);
		return (tmp >= ROADS && tmp <= INTERSECTION);
	}

	public static boolean roadConnectsEast(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != VROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != VBRIDGE));
	}

	public static boolean roadConnectsNorth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != HROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != ROADBASE));
	}

	public static boolean roadConnectsSouth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != HROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != ROADBASE));
	}

	public static boolean roadConnectsWest(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= ROADBASE && tile <= VROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)	
			) &&
			(tile != VROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != VBRIDGE));
	}
	
	
	
	
	public static boolean isBigRoadDynamic(int tile)
	{
		int tmp = neutralizeRoad(tile);
		return (tmp >= BIGROADS && tmp <= BIGINTERSECTION);
	}

	public static boolean bigroadConnectsEast(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= BIGROADBASE && tile <= BIGVROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != BIGVROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != BIGVBRIDGE));
	}

	public static boolean bigroadConnectsNorth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= BIGROADBASE && tile <= BIGVROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != BIGHROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != BIGROADBASE));
	}

	public static boolean bigroadConnectsSouth(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == HRAILROAD) ||
			(tile >= BIGROADBASE && tile <= BIGVROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)
			) &&
			(tile != BIGHROADPOWER) &&
			(tile != VRAILROAD) &&
			(tile != BIGROADBASE));
	}

	public static boolean bigroadConnectsWest(int tile)
	{
		tile = neutralizeRoad(tile);
		return (((tile == VRAILROAD) ||
			(tile >= BIGROADBASE && tile <= BIGVROADPOWER) || (tile >= NARROWINGS && tile <= NARROWINGS10)	
			) &&
			(tile != BIGVROADPOWER) &&
			(tile != HRAILROAD) &&
			(tile != BIGVBRIDGE));
	}
	
	
	

	public static boolean isRail(int tile) //TODO add new tiles (rail and bigRoad crossover)
	{
		assert (tile & LOMASK) == tile;

		return ((tile >= RAILBASE && tile < RESBASE) || tile == 1215 || tile==1216 || tile==1244);
	}

	public static boolean isRailAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= RAILBASE && tile < RESBASE)
			|| (tile == RAILHPOWERV)
			|| (tile == RAILVPOWERH);
	}

	public static boolean isRailDynamic(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= LHRAIL && tile <= LVRAIL10);
	}

	public static boolean isStationDynamic(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= STATION && tile <= STATIONINTERSECTION);
	}

	
	
	public static boolean railConnectsEast(int tile)
	{
		tile = neutralizeRoad(tile);
		return ((tile >= RAILHPOWERV && tile <= VRAILROAD || tile >= STATION && tile <= STATIONINTERSECTION) &&
			tile != RAILVPOWERH &&
			tile != VRAILROAD &&
			tile != VRAIL 
		);
	}

	public static boolean railConnectsNorth(int tile)
	{
		tile = neutralizeRoad(tile);
		return ((tile >= RAILHPOWERV && tile <= VRAILROAD || tile >= STATION && tile <= STATIONINTERSECTION) &&
			tile != RAILHPOWERV &&
			tile != HRAILROAD &&
			tile != HRAIL);
	}

	public static boolean railConnectsSouth(int tile)
	{
		tile = neutralizeRoad(tile);
		return ((tile >= RAILHPOWERV && tile <= VRAILROAD || tile >= STATION && tile <= STATIONINTERSECTION) &&
			tile != RAILHPOWERV &&
			tile != HRAILROAD &&
			tile != HRAIL);
	}

	public static boolean railConnectsWest(int tile)
	{
		tile = neutralizeRoad(tile);
		return ((tile >= RAILHPOWERV && tile <= VRAILROAD || tile >= STATION && tile <= STATIONINTERSECTION) &&
			tile != RAILVPOWERH &&
			tile != VRAILROAD &&
			tile != VRAIL);
	}

	public static boolean isWireDynamic(int tile)
	{
		assert (tile & LOMASK) == tile;

		return (tile >= LHPOWER && tile <= LVPOWER10);
	}

	public static boolean wireConnectsEast(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != HPOWER &&
			ntile != HROADPOWER &&
			ntile != RAILHPOWERV);
	}

	public static boolean wireConnectsNorth(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != VPOWER &&
			ntile != VROADPOWER &&
			ntile != RAILVPOWERH);
	}

	public static boolean wireConnectsSouth(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != VPOWER &&
			ntile != VROADPOWER &&
			ntile != RAILVPOWERH);
	}

	public static boolean wireConnectsWest(int tile)
	{
		int ntile = neutralizeRoad(tile);
		return (isConductive(tile) &&
			ntile != HPOWER &&
			ntile != HROADPOWER &&
			ntile != RAILHPOWERV);
	}

	public static boolean isCommercialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.getBooleanAttribute("commercial-zone");
		}
		return false;
	}

	public static boolean isHospitalOrChurch(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= HOSPITAL &&
			tile < COMBASE;
	}

	/**
	 * Checks whether the tile is defined with the "industrial-zone" attribute.
	 * Note: the old version of this function erroneously included the coal power
	 * plant smoke as an industrial zone.
	 */
	public static boolean isIndustrialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.getBooleanAttribute("industrial-zone");
		}
		return false;
	}


    public static boolean isCityHallBuilding(int tile) {
        assert (tile & LOMASK) == tile;

        TileSpec ts = Tiles.get(tile);
        if (ts != null) {
            if (ts.owner != null) {
                ts = ts.owner;
            }
            return ts.getBooleanAttribute("city-hall");
        }
        return false;
    }

    public static boolean isUniversityA(int tile) {
        assert (tile & LOMASK) == tile;

        TileSpec ts = Tiles.get(tile);
        if (ts != null) {
            if (ts.owner != null) {
                ts = ts.owner;
            }
            return ts.getBooleanAttribute("universitya");
        }
        return false;
    }   
    
    public static boolean isUniversityB(int tile) {
        assert (tile & LOMASK) == tile;

        TileSpec ts = Tiles.get(tile);
        if (ts != null) {
            if (ts.owner != null) {
                ts = ts.owner;
            }
            return ts.getBooleanAttribute("universityb");
        }
        return false;
    }     
    

    public static boolean isResidentialClear(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE && tile <= RESBASE+8;
	}

	/** Note: does not include hospital/church.
	 * @see #isHospitalOrChurch
	 */
	public static boolean isResidentialZone(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE &&
			tile < HOSPITAL;
	}

	// includes hospital/church.
	public static boolean isResidentialZoneAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		if (ts != null) {
			if (ts.owner != null) {
				ts = ts.owner;
			}
			return ts.getBooleanAttribute("residential-zone");
		}
		return false;
	}

	/** Tile represents a part of any sort of building. */
	public static boolean isZoneAny(int tile)
	{
		assert (tile & LOMASK) == tile;

		return tile >= RESBASE;
	}

	public static boolean isZoneCenter(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec spec = Tiles.get(tile);
		return spec != null && spec.zone;
	}

	/**
	 * Converts a road tile value with traffic to the equivalent
	 * road tile without traffic.
	 */
	public static char neutralizeRoad(int tile)
	{
		assert (tile & LOMASK) == tile;

		if (tile >= ROADBASE && tile <= LASTROAD) {
			tile = ((tile - ROADBASE) & 0xf) + ROADBASE;
		}
		if (tile >= BIGROADBASE && tile <= BIGLASTROAD) {
			tile = ((tile - BIGROADBASE) & 0xf) + ROADBASE;
		}
		return (char)tile;
	}

	/**
	 * Determine the population level of a Residential zone
	 * tile. Note: the input tile MUST be a full-size res zone,
	 * it cannot be an empty zone.
	 * @return int multiple of 8 between 16 and 40.
	 */
	public static int residentialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation();
	}

	/**
	 * Determine the population level of a Commercial zone
	 * tile.
	 * The input tile MAY be an empty zone.
	 * @return int between 0 and 5.
	 */
	public static int commercialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation() / 8;
	}

	/**
	 * Determine the population level of an Industrial zone tile.
	 * The input tile MAY be an empty zone.
	 * @return int between 0 and 4.
	 */
	public static int industrialZonePop(int tile)
	{
		assert (tile & LOMASK) == tile;

		TileSpec ts = Tiles.get(tile);
		return ts.getPopulation() / 8;
	}
}
