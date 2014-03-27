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

import micropolisj.engine.techno.*;

import java.io.*;
import java.util.*;

import micropolisj.engine.techno.BuildingTechnology;

import micropolisj.engine.techno.GeneralTechnology;

import micropolisj.engine.techno.StreetUpgradeTech;

import micropolisj.engine.techno.Technology;

import static micropolisj.engine.TileConstants.*;

/**
 * The main simulation engine for Micropolis.
 * The front-end should call animate() periodically
 * to move the simulation forward in time.
 */
public class Micropolis
{
	static final Random DEFAULT_PRNG = new Random();

	Random PRNG;

	// full size arrays
	char [][] map;
	boolean [][] powerMap;

	/**
	 * For each section of the city, the land value of the city (0-250).
	 * 0 is lowest land value; 250 is maximum land value.
	 * Updated each cycle by ptlScan().
	 */
	int [][] landValueMem;

	/**
	 * For each section of the city, the pollution level of the city (0-255).
	 * 0 is no pollution; 255 is maximum pollution.
	 * Updated each cycle by ptlScan(); affects land value.
	 */
	public int [][] pollutionMem;

	/**
	 * For each section of the city, the crime level of the city (0-250).
	 * 0 is no crime; 250 is maximum crime.
	 * Updated each cycle by crimeScan(); affects land value.
	 */
	public int [][] crimeMem;

	/**
	 * For each section of the city, the population density (0-?).
	 * Used for map overlays and as a factor for crime rates.
	 */
	public int [][] popDensity;

	/**
	 * For each section of the city, the traffic density (0-255).
	 * If less than 64, no cars are animated.
	 * If between 64 and 192, then the "light traffic" animation is used.
	 * If 192 or higher, then the "heavy traffic" animation is used.
	 */
	int [][] trfDensity;
	int [][] trfMem;

	// quarter-size arrays

	/**
	 * For each section of the city, an integer representing the natural
	 * land features in the vicinity of this part of the city.
	 */
	int [][] terrainMem;

	// eighth-size arrays

	/**
	 * For each section of the city, the rate of growth.
	 * Capped to a number between -200 and 200.
	 * Used for reporting purposes only; the number has no affect.
	 */
	public int [][] rateOGMem; //rate of growth
    public ArrayList<CityLocation> cityHallList = new ArrayList<CityLocation>();

	int [][] fireStMap;      //firestations- cleared and rebuilt each sim cycle
	public int [][] fireRate;       //firestations reach- used for overlay graphs
	int [][] policeMap;      //police stations- cleared and rebuilt each sim cycle
	public int [][] policeMapEffect;//police stations reach- used for overlay graphs
    public int educationValue;
    public int cultureValue;

	int [][] cityhallMap;
	int [][] lastWay;
	public int [][] cityhallEffect; //unib reach- used for overlay graphs

	/** For each section of city, this is an integer between 0 and 64,
	 * with higher numbers being closer to the center of the city. */
	int [][] comRate;

	static final int DEFAULT_WIDTH = 120;
	static final int DEFAULT_HEIGHT = 100;

	public final CityBudget budget = new CityBudget(this);
	public boolean autoBulldoze = true;
	public boolean autoBudget = false;
	public Speed simSpeed = Speed.NORMAL;
    public boolean isPaused;
    public Speed oldSpeed = simSpeed;
    public boolean noDisasters = false;

	public int gameLevel;

    public int nCheats = 0;
    public int cheatedPopulation = 0;

    boolean autoGo;




    // census numbers, reset in phase 0 of each cycle, summed during map scan




	int poweredZoneCount;
	int unpoweredZoneCount;
	int roadTotal;
	int bigroadTotal;
	int railTotal;
	int stationTotal;
	int firePop;
	int resZoneCount;
	int comZoneCount;
	int indZoneCount;
	int resPop;
	int comPop;
	int indPop;
	int hospitalCount;
	int churchCount;
	int policeCount;
	int schoolCount;
	int uniaCount;
	int unibCount;
	int cityhallCount;
    int cityhallCountMem;
	int openairCount;
	int museumCount;
	int fireStationCount;
	int stadiumCount;
	int coalCount;
	int nuclearCount;
	int seaportCount;
	int airportCount;
	int solarCount;
	int windCount;
	int noWay;
	int longWay;
	

	int totalPop;
	int lastCityPop;

	// used in generateBudget()
	int lastRoadTotal;
	int lastRailTotal;
	int lastStationTotal;
	int lastBigRoadTotal;
	int lastTotalPop;
	int lastFireStationCount;
	int lastPoliceCount;
	int lastSchoolCount;
	int lastMuseumCount;
	int lastStadiumCount;
	int lastUniACount;
	int lastUniBCount;
	int lastOpenAirCount;
	int lastCityHallCount;
	int lastSolarCount;
	int lastWindCount;

	int trafficMaxLocationX;
	int trafficMaxLocationY;
	int pollutionMaxLocationX;
	int pollutionMaxLocationY;
	int crimeMaxLocationX;
    int crimeMaxLocationY;
	public int centerMassX;
	public int centerMassY;
	CityLocation meltdownLocation;  //may be null
	CityLocation crashLocation;     //may be null

	int needHospital; // -1 too many already, 0 just right, 1 not enough
	int needChurch;   // -1 too many already, 0 just right, 1 not enough

	int crimeAverage;
    int cultureAverage;
    int educationAverage;
	int pollutionAverage;
	int landValueAverage;
	int trafficAverage;

	int resValve;   // ranges between -2000 and 2000, updated by setValves
	int comValve;   // ranges between -1500 and 1500
	int indValve;   // ranges between -1500 and 1500

	boolean resCap;  // residents demand a stadium, caps resValve at 0
	boolean comCap;  // commerce demands airport,   caps comValve at 0
	boolean indCap;  // industry demands sea port,  caps indValve at 0
	int crimeRamp;
	int polluteRamp;
    int educationRamp;


    // science/tech stuff
    ArrayList<BuildingTechnology> buildingTechs;
    public ArrayList<GeneralTechnology> eetechs;
    public ArrayList<GeneralTechnology> infraTechs;
    public double technologyEEPoints;
    public double technologyInfraPoints;
    public GeneralTechnology selectedInfraTech = null;
    public void setSelectedInfraTech(GeneralTechnology selectedInfraTech) {
		this.selectedInfraTech = selectedInfraTech;
	}

	public GeneralTechnology getSelectedInfraTech() {
		return selectedInfraTech;
	}
    public GeneralTechnology getSelectedEETech() {
        return selectedEETech;
    }

	public GeneralTechnology selectedEETech = null;

    public BuildingTechnology windTech;
    public BuildingTechnology solarTech;
    public BuildingTechnology airportTech;
    public BuildingTechnology twoLaneRoadTech;
    public StreetUpgradeTech streetUpgradeTech;
    public RailUpgradeTech railUpgradeTech;
    public FireUpdateTech fireUpdateTech;
    public PoliceUpgradeTech policeUpgradeTech;
    public ReducePollutionTech reducePollutionTech;
    public ImproveWindSolarTech improveWindSolarTech;
    public ProbabilityMeltdown meltdownTech;

	//
	// budget stuff
	//
	public int cityTax = 7;
	public double roadPercent = 1.0;
	public double policePercent = 1.0;
	public double firePercent = 1.0;
	public double schoolPercent = 1.0;
	public double culturePercent = 1.0;
	
	int taxEffect = 7;
	int roadEffect = 32;
	int policeEffect = 1000;
	int fireEffect = 1000;
    int educationEffect = 1000;
    int cultureEffect = 1000;
    
    public int firesccount      =1;
    public int policesccount    =1;
    public int windsolarsccount =1;
    public int pollutionsccount =1;
    public int streetsccount    =1;
    public int railsccount      =1;

	int cashFlow; //net change in totalFunds in previous year

	boolean newPower;

	int floodCnt; //number of turns the flood will last
	int floodX;
	int floodY;

	public int cityTime;  //counts "weeks" (actually, 1/48'ths years)
	int scycle; //same as cityTime, except mod 1024
	int fcycle; //counts simulation steps (mod 1024)
	int acycle; //animation cycle (mod 960)
	
	public HashMap<CityLocation,Integer> visits=new HashMap<CityLocation,Integer>();
	public Vector<Vector<CityLocation>> paths=new Vector<Vector<CityLocation>>();
	public HashMap<CityLocation,Integer> visitNew=new HashMap<CityLocation,Integer>();

	public CityEval evaluation;

	ArrayList<Sprite> sprites = new ArrayList<Sprite>();

	static final int VALVERATE = 2;
	public static final int CENSUSRATE = 4;
	static final int TAXFREQ = 48;

	public void spend(int amount)
	{
		budget.totalFunds -= amount;
		fireFundsChanged();
	}
	
	public void paths(Vector<CityLocation> way) {
		paths.add(way);
	}

    public void incNCheats() {
        nCheats++;
    }
    
    public void testPrint(String s){
    	System.out.println(s);
    }
    
//    public void setSelectedInfraTech(GeneralTechnology t){
//    	// System.out.println("select inra tech: + " t.getName());
//    	if(this.selectedInfraTech != null) System.out.println("selected infra tech: " + this.selectedInfraTech.getName());
//    	this.selectedInfraTech = t;
//    	if(this.selectedInfraTech != null) System.out.println("selected infra tech: " + this.selectedInfraTech.getName());
//    	
//    }

    public void incCityPopulation() {
        cheatedPopulation += 20000;
        int newPop = (resPop + comPop * 8 + indPop * 8) * 20;
        lastCityPop = newPop + cheatedPopulation;
    }


    public void resetNCheats() {
        nCheats = 0;
    }

    public Micropolis() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Micropolis(int width, int height)
	{
		PRNG = DEFAULT_PRNG;
		evaluation = new CityEval(this);
		init(width, height);
		initTileBehaviors();
	}

	protected void init(int width, int height)
	{
		map = new char[height][width];
		powerMap = new boolean[height][width];

		landValueMem = new int[height][width];
		pollutionMem = new int[height][width];
		crimeMem = new int[height][width];
		popDensity = new int[height][width];
		trfDensity = new int[height][width];
		trfMem = new int[height][width];
		terrainMem = new int[height][width];
		rateOGMem = new int[height][width];
		fireStMap = new int[height][width];
		policeMap = new int[height][width];
		cityhallMap = new int[height][width];
		policeMapEffect = new int[height][width];
		fireRate = new int[height][width];
		comRate = new int[height][width];
		lastWay = new int[height][width];

        technologyEEPoints = 0;
        technologyInfraPoints = 0;


        initTechs();


	}


    void initTechs(){
        buildingTechs = new ArrayList<BuildingTechnology>();
        eetechs = new ArrayList<GeneralTechnology>();
        infraTechs = new ArrayList<GeneralTechnology>();

        windTech = new BuildingTechnology(this,2000.0, "wind description", "Wind Power Plant Tech", MicropolisTool.WIND,MicropolisMessage.WIND_RESEARCH);
        buildingTechs.add(windTech);
        eetechs.add(windTech);


        solarTech = new BuildingTechnology(this,2000.0, "solar description", "Solar Power Plant Tech", MicropolisTool.SOLAR,MicropolisMessage.SOLAR_RESEARCH);
        buildingTechs.add(solarTech);
        eetechs.add(solarTech);


        airportTech = new BuildingTechnology(this,2000.0, "airport tech description", "Airport Tech", MicropolisTool.AIRPORT,MicropolisMessage.AIRPORT_RESEARCH);
        buildingTechs.add(airportTech);
        infraTechs.add(airportTech);
        
        twoLaneRoadTech = new BuildingTechnology(this, 200, "two lane description", "two lane Tech", MicropolisTool.BIGROADS,MicropolisMessage.TWOLANEROAD_RESEARCH);
        buildingTechs.add(twoLaneRoadTech);
        infraTechs.add(twoLaneRoadTech);
        
        
        meltdownTech = new ProbabilityMeltdown(this, 1500, "meltdown description", "meltdown Tech", MicropolisMessage.NUCLEAR_UPGRADE);
        eetechs.add(meltdownTech);
    


        streetUpgradeTech = new StreetUpgradeTech(this,800, "street upgrade description", "street upgrade Tech",MicropolisMessage.ROAD_UPGRADE);
        infraTechs.add(streetUpgradeTech);

        railUpgradeTech = new RailUpgradeTech(this,400, "rail upgrade description", "rail upgrade tech",MicropolisMessage.RAIL_UPGRADE);
        infraTechs.add(railUpgradeTech);
        
        fireUpdateTech = new FireUpdateTech(this,400, "fire upgrade description", "fire upgrade tech",MicropolisMessage.FIRE_UPGRADE);
        infraTechs.add(fireUpdateTech);
        
        policeUpgradeTech = new PoliceUpgradeTech(this,400, "police upgrade description", "police upgrade tech",MicropolisMessage.POLICE_UPGRADE);
        infraTechs.add(policeUpgradeTech);
        
        reducePollutionTech = new ReducePollutionTech(this,800, "reduce pollution description", "reduce pollution tech",MicropolisMessage.POLLUTION_UPGRADE);
        eetechs.add(reducePollutionTech);
        
        improveWindSolarTech = new ImproveWindSolarTech(this,800, "improve wind and solar power plants description", "wind solar upgrade tech",MicropolisMessage.SOLARWIND_UPGRADE);
        eetechs.add(improveWindSolarTech);
        
        selectedInfraTech = null;
        selectedEETech = null;
        
        System.out.println("initialize Technology Objects.");

    }

	void fireCensusChanged()
	{
		for (Listener l : listeners) {
			l.censusChanged();
		}
	}

	void fireCityMessage(MicropolisMessage message, CityLocation loc)
	{
		for (Listener l : listeners) {
			l.cityMessage(message, loc);
		}
	}

	void fireCitySound(Sound sound, CityLocation loc)
	{
		for (Listener l : listeners) {
			l.citySound(sound, loc);
		}
	}

	void fireDemandChanged()
	{
		for (Listener l : listeners) {
			l.demandChanged();
		}
	}

	void fireEarthquakeStarted()
	{
		for (EarthquakeListener l : earthquakeListeners) {
			l.earthquakeStarted();
		}
	}

	void fireEvaluationChanged()
	{
		for (Listener l : listeners) {
			l.evaluationChanged();
		}
	}

	void fireFundsChanged()
	{
		for (Listener l : listeners) {
			l.fundsChanged();
		}
	}

	void fireMapOverlayDataChanged(MapState overlayDataType)
	{
		for (MapListener l : mapListeners) {
			l.mapOverlayDataChanged(overlayDataType);
		}
	}

	void fireOptionsChanged()
	{
		for (Listener l : listeners)
		{
			l.optionsChanged();
		}
	}

	void fireSpriteMoved(Sprite sprite)
	{
		for (MapListener l : mapListeners)
		{
			l.spriteMoved(sprite);
		}
	}

	void fireTileChanged(int xpos, int ypos)
	{
		for (MapListener l : mapListeners)
		{
			l.tileChanged(xpos, ypos);
		}
	}

	void fireWholeMapChanged()
	{
		for (MapListener l : mapListeners)
		{
			l.wholeMapChanged();
		}
	}

	ArrayList<Listener> listeners = new ArrayList<Listener>();
	ArrayList<MapListener> mapListeners = new ArrayList<MapListener>();
	ArrayList<EarthquakeListener> earthquakeListeners = new ArrayList<EarthquakeListener>();

	public void addListener(Listener l)
	{
		this.listeners.add(l);
	}

	public void removeListener(Listener l)
	{
		this.listeners.remove(l);
	}

	public void addEarthquakeListener(EarthquakeListener l)
	{
		this.earthquakeListeners.add(l);
	}

	public void removeEarthquakeListener(EarthquakeListener l)
	{
		this.earthquakeListeners.remove(l);
	}

	public void addMapListener(MapListener l)
	{
		this.mapListeners.add(l);
	}

	public void removeMapListener(MapListener l)
	{
		this.mapListeners.remove(l);
	}

    public void destroyEverything() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int t = getTile(x, y);
                if (t == NUCLEAR) {
                    doMeltdown(x, y);
                }
                if (isArsonable(t) && isConstructed(t)) {
                    makeQuietExplosion(x, y);
                }
            }
        }
    }

    /**
     * The listener interface for receiving miscellaneous events that occur
	 * in the Micropolis city.
	 * Use the Micropolis class's addListener interface to register an object
	 * that implements this interface.
	 */
	public interface Listener
	{
		void cityMessage(MicropolisMessage message, CityLocation loc);
		void citySound(Sound sound, CityLocation loc);

		/**
		 * Fired whenever the "census" is taken, and the various historical
		 * counters have been updated. (Once a month in game.)
		 */
		void censusChanged();

		/**
		 * Fired whenever resValve, comValve, or indValve changes.
		 * (Twice a month in game.) */
		void demandChanged();

		/**
		 * Fired whenever the city evaluation is recalculated.
		 * (Once a year.)
		 */
		void evaluationChanged();

		/**
		 * Fired whenever the mayor's money changes.
		 */
		void fundsChanged();

		/**
		 * Fired whenever autoBulldoze, autoBudget, noDisasters,
		 * or simSpeed change.
		 */
		void optionsChanged();
	}

	public int getWidth()
	{
		return map[0].length;
	}

	public int getHeight()
	{
		return map.length;
	}

	public char getTile(int xpos, int ypos)
	{
		return (char)(map[ypos][xpos] & LOMASK);
	}
	public char getTile(CityLocation loc)
	{
		return (char)(map[loc.y][loc.x] & LOMASK);
	}

	public char getTileRaw(int xpos, int ypos)
	{
		return map[ypos][xpos];
	}

	boolean isTileDozeable(ToolEffectIfc eff)
	{
		int myTile = eff.getTile(0, 0);
		TileSpec ts = Tiles.get(myTile);
		if (ts.canBulldoze) {
			return true;
		}

		if (ts.owner != null) {
			// part of a zone; only bulldozeable if the owner tile is
			// no longer intact.

			int baseTile = eff.getTile(-ts.ownerOffsetX, -ts.ownerOffsetY);
			return !(ts.owner.tileNumber == baseTile);
		}

		return false;
	}

	boolean isTileDozeable(int xpos, int ypos)
	{
		return isTileDozeable(
			new ToolEffect(this, xpos, ypos)
			);
	}

	public boolean isTilePowered(int xpos, int ypos)
	{
		return (getTileRaw(xpos, ypos) & PWRBIT) == PWRBIT;
	}

	public void setTile(int xpos, int ypos, char newTile)
	{
		if (map[ypos][xpos] != newTile)
		{
			map[ypos][xpos] = newTile;
			fireTileChanged(xpos, ypos);
		}
	}

	final public boolean testBounds(int xpos, int ypos)
	{
		return xpos >= 0 && xpos < getWidth() &&
			ypos >= 0 && ypos < getHeight();
	}

	final boolean hasPower(int x, int y)
	{
		return powerMap[y][x];
	}

	/**
	 * Checks whether the next call to animate() will collect taxes and
	 * process the budget.
	 */
	public boolean isBudgetTime()
	{
		return (
			cityTime != 0 &&
			(cityTime % TAXFREQ) == 0 &&
			((fcycle + 1) % 16) == 10 &&
			((acycle + 1) % 2) == 0
			);
	}

	void step()
	{
		fcycle = (fcycle + 1) % 1024;
		simulate(fcycle % 16);
	}

	void clearCensus()
	{
		
		poweredZoneCount = 0;
		unpoweredZoneCount = 0;
		firePop = 0;
		roadTotal = 0;
		bigroadTotal = 0;
		railTotal = 0;
		stationTotal = 0; 
		resPop = 0;
		comPop = 0;
		indPop = 0;
		resZoneCount = 0;
		comZoneCount = 0;
		indZoneCount = 0;
		hospitalCount = 0;
		churchCount = 0;
		policeCount = 0;
		schoolCount = 0;
		museumCount = 0;
		uniaCount = 0;
		unibCount = 0;
		openairCount = 0;
		cityhallCount = 0;
		fireStationCount = 0;
		stadiumCount = 0;
		coalCount = 0;
		nuclearCount = 0;
		seaportCount = 0;
		airportCount = 0;
		noWay = 0;
		longWay = 0;
		paths.clear();
		powerPlants.clear();
        cityHallList.clear();
        
        HashMap<CityLocation,Integer> zw =new HashMap<CityLocation,Integer>();
        for (CityLocation f : visitNew.keySet()) {
        	if (visits.get(f)==null) {
        		zw.put(f,visitNew.get(f));
        	} else {
        		zw.put(f,rd(visits.get(f)+visitNew.get(f),2));
        	}
        }
        visits =new HashMap<CityLocation,Integer>(zw);
        visitNew.clear();
        solarCount = 0;
		windCount = 0;

		for (int y = 0; y < fireStMap.length; y++) {
			for (int x = 0; x < fireStMap[y].length; x++) {
				fireStMap[y][x] = 0;
				policeMap[y][x] = 0;
				cityhallMap[y][x] = 0;
			}
		}
	}

	void simulate(int mod16)
	{
		final int band = getWidth() / 8;

		switch (mod16)
		{
		case 0:
			scycle = (scycle + 1) % 1024;
			cityTime++;
			if (scycle % 2 == 0) {
				setValves();
			}
			clearCensus();
			break;

		case 1:
			mapScan(0 * band, 1 * band);
			break;

		case 2:
			mapScan(1 * band, 2 * band);
			break;

		case 3:
			mapScan(2 * band, 3 * band);
			break;

		case 4:
			mapScan(3 * band, 4 * band);
			break;

		case 5:
			mapScan(4 * band, 5 * band);
			break;

		case 6:
			mapScan(5 * band, 6 * band);
			break;

		case 7:
			mapScan(6 * band, 7 * band);
			break;

		case 8:
			mapScan(7 * band, getWidth());
			break;

		case 9:
			if (cityTime % CENSUSRATE == 0) {
				takeCensus();

				if (cityTime % (CENSUSRATE*12) == 0) {
					takeCensus2();
				}

				fireCensusChanged();
			}

			collectTaxPartial();
            spendTechnologyPoints();

			if (cityTime % TAXFREQ == 0) {
				collectTax();
				evaluation.cityEvaluation();
			}
			break;

		case 10:
			if (scycle % 5 == 0) {  // every ~10 weeks
				decROGMem();
			}
			copyTrafficMem();
			decTrafficMem();
			fireMapOverlayDataChanged(MapState.TRAFFIC_OVERLAY); //TDMAP
			fireMapOverlayDataChanged(MapState.TRANSPORT);       //RDMAP
			fireMapOverlayDataChanged(MapState.ALL);             //ALMAP
			fireMapOverlayDataChanged(MapState.RESIDENTIAL);     //REMAP
			fireMapOverlayDataChanged(MapState.COMMERCIAL);      //COMAP
			fireMapOverlayDataChanged(MapState.INDUSTRIAL);      //INMAP
			doMessages();
			break;

		case 11:
			powerScan();
			fireMapOverlayDataChanged(MapState.POWER_OVERLAY);
			newPower = true;
			break;

		case 12:
			ptlScan();
			break;
		case 13:
            popDenScan();
			break;
		case 14:
            crimeScan();
			break;
        case 15:
            fireAnalysis();
            doDisasters();
			break;

		default:
			throw new Error("unreachable");
		}
	}
	
	private void copyTrafficMem() {
		for (int y=0;y<getHeight();y++) {
			for (int x=0;x<getWidth();x++) {
				trfMem[y][x]=trfDensity[y][x];
			}
		}
	}

	private int computePopDen(int x, int y, char tile)
	{
		if (tile == RESCLR)
			return doFreePop(x, y);

		if (tile < COMBASE)
			return residentialZonePop(tile);

		if (tile < INDBASE)
			return commercialZonePop(tile) * 8;

		if (tile < PORTBASE)
			return industrialZonePop(tile) * 8;

		return 0;
	}

    private void areaSpread(int[][] pol, CityLocation loc, int size, int randomRange){
        pol[loc.y][loc.x] = pol[loc.y][loc.x]/10;
        int polutionAdd = pol[loc.y][loc.x]*9; //spreading pollution is 1/3 the original pollution on that point

        polutionAdd /= Math.ceil(size * size); // same pollution for all fields
        int startx = loc.x - size/2;
        int starty = loc.y - size/2;
        for(int y = starty; y < starty + size; y++){
            for(int x = startx; x < startx + size; x++){
                if(onMap(x,y)){
                    pol[y][x] += (int) clamp(polutionAdd + PRNG.nextInt(randomRange) - randomRange/2, 1, 254);
                }
            }
        }
    }

    private void gaussianSpread(int [][] matrix, int standardDeviation, CityLocation loc){
        int intensityPool = matrix[loc.y][loc.x];
        while(intensityPool > 1){
            double x_r = PRNG.nextGaussian();
            double y_r = PRNG.nextGaussian();
            double intensity_r = PRNG.nextGaussian();
            int x = (int) ((standardDeviation * x_r) + loc.x);
            int y = (int) ((standardDeviation * y_r) + loc.y);

            int intensity = (int) Math.ceil((2.0 * intensity_r) + 4.0);
            intensityPool -= intensity;
            if(onMap(x,y) == true){
                matrix[y][x] += intensity;
            }
        }
    }


    private void spreadEffect(int[][] pol, int size, int randomRange, int minValue){
        //TODO: GAUSSIAN SPREAD
        final int h = pol.length;
        final int w = pol.length;

        // check every field, if there is higher pollution divide it by 4. 3/4 will spread to neigboring tiles
        // 10x10 surroundings
        ArrayList<CityLocation> highPollutionLocs = new ArrayList<CityLocation>();
        for (int y = 1; y < h; y++)
        {
            for (int x = 1; x < w; x++)
            {
                if(pol[y][x] > 5) highPollutionLocs.add(new CityLocation(x,y));
            }
        }
        for(CityLocation l : highPollutionLocs){
            //areaSpread(pol, l, size, randomRange);
            gaussianSpread(pol, 6, l);
        }
    }

	private static int [][] doSmooth(int [][] tem)
	{
		final int h = tem.length;
		final int w = tem[0].length;
		int [][] tem2 = new int[h][w];

		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				int z = tem[y][x];
				if (x > 0)
					z += tem[y][x-1];
				if (x + 1 < w)
					z += tem[y][x+1];
				if (y > 0)
					z += tem[y-1][x];
				if (y + 1 < h)
					z += tem[y+1][x];

                // new adding values from diagonals
               /* if (x > 0 && y > 0)
                    z += tem[y-1][x-1];
                if (x + 1 < w && y > 0)
                    z += tem[y-1][x+1];
                if (y + 1 < h && x + 1 < w)
                    z += tem[y+1][x+1];
                if (y + 1 < h && x > 0)
                    z += tem[y+1][x-1];*/

				z /= 8;
				if (z > 255)
					z = 255;
				tem2[y][x] = z;
			}
		}

		return tem2;
	}

	public void calculateCenterMass()
	{
		popDenScan();
	}

    // calculates centerMass and popDensity
	private void popDenScan()
	{
		int xtot = 0;
		int ytot = 0;
		int zoneCount = 0;
		int width = getWidth();
		int height = getHeight();
		int [][] tem = new int[(height)][(width)];

        // iterate all tiles on map
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				char tile = getTile(x, y);
                // only continue if tile is a zone center
				if (isZoneCenter(tile)){
                    // get density of tile from computePopDen
					int den = computePopDen(x, y, tile);
					if (den > 254)
						den = 254;
                    // write the density into a new array for each tile
					tem[y][x] = den*40;
					xtot += x;
					ytot += y;
					zoneCount++;
				}
			}
		}

        //smoothing the density array
		tem = doSmooth(tem);
        tem = doSmooth(tem);
        tem = doSmooth(tem);


		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				popDensity[y][x] = tem[y][x];
			}
		}

		distIntMarket(); //set ComRate

        // new centermass calculation
        // check if there are city halls build already

		// find center of mass for city
		if (zoneCount != 0)
		{
			centerMassX = xtot / zoneCount;
			centerMassY = ytot / zoneCount;
		}
		else if(cityhallCount < 1)
		{
			centerMassX = (width+1)/2;
			centerMassY = (height+1)/2;
		} else
        {
            // using locations of city hall to set centerMass


        }

		fireMapOverlayDataChanged(MapState.POPDEN_OVERLAY);     //PDMAP
		fireMapOverlayDataChanged(MapState.GROWTHRATE_OVERLAY); //RGMAP
	}

	private void distIntMarket()
	{
		for (int y = 0; y < comRate.length; y++)
		{
			for (int x = 0; x < comRate[y].length; x++)
			{
				int z = getDisCC(x, y);
				z /= 4;
				z = 64 - z;
				comRate[y][x] = z;
			}
		}
	}

	//tends to empty RateOGMem[][]
	private void decROGMem()
	{
		for (int y = 0; y < rateOGMem.length; y++)
		{
			for (int x = 0; x < rateOGMem[y].length; x++)
			{
				int z = rateOGMem[y][x];
				if (z == 0)
					continue;

				if (z > 0)
				{
					rateOGMem[y][x]--;
					if (z > 200)
					{
						rateOGMem[y][x] = 200; //prevent overflow?
					}
					continue;
				}

				if (z < 0)
				{
					rateOGMem[y][x]++;
					if (z < -200)
					{
						rateOGMem[y][x] = -200;
					}
					continue;
				}
			}
		}
	}

	//tends to empty trfDensity
	private void decTrafficMem()
	{
		for (int y = 0; y < trfDensity.length; y++)
		{
			for (int x = 0; x < trfDensity[y].length; x++)
			{
				//trfDensity[y][x]*=2;
				trfDensity[y][x]/=3;
				/*original functionint z = trfDensity[y][x];
				if (z != 0)
				{
					
					  if (z > 200)
						trfDensity[y][x] = z - 34;
					else if (z > 24)
						trfDensity[y][x] = z - 24;
					else
						trfDensity[y][x] = 0;
				}*/
			}
		}
	}

	void crimeScan()
	{
		policeMap=doSmooth(policeMap);


		for (int sy = 0; sy < policeMap.length; sy++) {
            for (int sx = 0; sx < policeMap[sy].length; sx++) {
				policeMapEffect[sy][sx] = policeMap[sy][sx];
			}
		}

        int count = 0;
		int sum = 0;
		int cmax = 0;
		for (int hy = 0; hy < landValueMem.length; hy++) {
			for (int hx = 0; hx < landValueMem[hy].length; hx++) {
				int val = landValueMem[hy][hx];
				if (val != 0) {
					count++;
					int z = 128 - val + popDensity[hy][hx];
					z = Math.min(300, z);
					z -= policeMap[hy][hx];
					z = clamp(z,0,250);
					crimeMem[hy][hx] = z;

					sum += z;
					if (z > cmax || (z == cmax && PRNG.nextInt(4) == 0)) {
						cmax = z;
						crimeMaxLocationX = hx;
						crimeMaxLocationY = hy;
					}
				}
				else {
					crimeMem[hy][hx] = 0;
				}
			}
		}



		if (count != 0)
			crimeAverage = sum / count;
		else
			crimeAverage = 0;

		fireMapOverlayDataChanged(MapState.POLICE_OVERLAY);
	}

	void doDisasters()
	{
        if (floodCnt > 0) {
            floodCnt--;
		}

		final int [] DisChance = { 480, 240, 60 };


        if (noDisasters)
            return;

        if (nCheats > 5) {
        	resetNCheats();
            makeEarthquake();
            return;
        }

        if (PRNG.nextInt(DisChance[gameLevel] + 1) != 0)
            return;

        switch (PRNG.nextInt(9))
		{
		case 0:
		case 1:
			setFire();
			break;
		case 2:
		case 3:
			makeFlood();
			break;
		case 4:
			break;
		case 5:
			makeTornado();
			break;
		case 6:
			makeEarthquake();
			break;
		case 7:
		case 8:
			if (pollutionAverage > 60) {
				makeMonster();
			}
			break;
		}
	}

	private int[][] smoothFirePoliceMap(int[][] omap)
	{
		int smX = omap[0].length;
		int smY = omap.length;
		int[][] nmap = new int[smY][smX];
		for (int sy = 0; sy < smY; sy++) {
			for (int sx = 0; sx < smX; sx++) {
				int edge = 0;
				if (sx > 0) { edge += omap[sy][sx-1]; }
				if (sx + 1 < smX) { edge += omap[sy][sx+1]; }
				if (sy > 0) { edge += omap[sy-1][sx]; }
				if (sy + 1 < smY) { edge += omap[sy+1][sx]; }
				edge = edge / 4 + omap[sy][sx];
				nmap[sy][sx] = edge / 2;
			}
		}
		return nmap;
	}

	void fireAnalysis()
	{
		fireStMap = smoothFirePoliceMap(fireStMap);
		fireStMap = smoothFirePoliceMap(fireStMap);
		fireStMap = smoothFirePoliceMap(fireStMap);
		for (int sy = 0; sy < fireStMap.length; sy++) {
			for (int sx = 0; sx < fireStMap[sy].length; sx++) {
				fireRate[sy][sx] = fireStMap[sy][sx];
			}
		}

		fireMapOverlayDataChanged(MapState.FIRE_OVERLAY);
	}

	private boolean testForCond(CityLocation loc, int dir)
	{

		boolean rv = false;
		if (onMap(loc,dir)) {
			CityLocation c=new CityLocation(goToAdj(loc, dir).x,goToAdj(loc, dir).y);
			rv = (isConductive(getTile(c.x, c.y)) && !hasPower(c.x, c.y));
		}
		return rv;
	}

	public boolean onMap(CityLocation loc, int dir) {
		switch(dir)
		{
		case 0:
			return (loc.y > 0);
		case 1:
			return (loc.x + 1 < getWidth());
		case 2:
			return (loc.y + 1 < getHeight());
		case 3:
			return (loc.x > 0);
		case 4:
			return true;
		}
		return false;
	}
	
	public boolean onMap(CityLocation loc) 	{
		return (loc.y > 0) && (loc.x + 1 < getWidth()) && (loc.y + 1 < getHeight()) && (loc.x > 0);
	}

    public boolean onMap(int x, int y) 	{
        return (y > 0) && (x + 1 < getWidth()) && (y + 1 < getHeight()) && (x > 0);
    }
	
	public void putVisits(CityLocation loc) {
		visitNew.put(loc,(dummySearch(visitNew,loc)+(1+lastCityPop/25000)));
	}
	
	public static CityLocation goToAdj(CityLocation loc, int dir)
	{
		CityLocation loci =new CityLocation(loc.x,loc.y);
		switch(dir)
		{
		case 0:
			loci.y--;
			return loci;
		case 1:
			loci.x++;
			return loci;
		case 2:
			loci.y++;
			return loci;
		case 3:
			loci.x--;
			return loci;
		}
		return loci;
	}

    void updateEducationAverage(int z){
        educationValue += z;
        //int educationBuildingCount = lastSchoolCount + lastUniACount + lastUniBCount;
        educationAverage = clamp(educationValue / Math.max(resPop / 5, 1), 0, 255);
        //System.out.println("educationAverage: " + educationAverage);
    }

    void updateCultureAverage(int z){
        cultureValue += z;
        cultureAverage = clamp(cultureValue / Math.max((resPop + comPop)/10, 1),0 , 255);
    }


    void powerScan()
    {
        // clear powerMap
        int localPower=0;
        Stack<CityLocation> toDo=new Stack<CityLocation>();
        HashSet<CityLocation> done=new HashSet<CityLocation>();
        CityLocation current=new CityLocation(1,1);
        for (boolean [] f : powerMap)
        {
            Arrays.fill(f, false);
        }

        while (!powerPlants.isEmpty()) {
            CityLocation loc = powerPlants.pop();
            if (!done.contains(loc)) {
                char g=getTile(loc.x,loc.y);

                if (g==NUCLEAR) {
                    localPower=2000;
                }
                else if (g==POWERPLANT) {
                    localPower=500; //original was 700
                }
                else if (g==SOLAR) {
                    localPower=325;
                }
                else if (g==WIND) {
                    localPower=30;
                }
                int numPower=0;
                toDo.add(loc);

                while (!toDo.isEmpty()) {
                    current=toDo.pop();
                    g=getTile(current.x,current.y);
                    if (g==NUCLEAR) {
                        localPower+=2000;
                        done.add(current);
                    } else if (g==POWERPLANT)  {
                        localPower+=500; //original was 700
                        done.add(current);
                    }
                    else if (g==SOLAR) {
                        localPower+=325;
                        done.add(current);
                    }
                    else if (g==WIND) {
                        localPower+=30;
                        done.add(current);
                    }



                    if (++numPower > localPower) {
                        // trigger notification
                        sendMessage(MicropolisMessage.BROWNOUTS_REPORT);
                        return;
                    }
                    powerMap[current.y][current.x] = true;
                    for (int dir=0;dir<4;dir++) {
                        if (testForCond(current, dir)) {
                            toDo.add(goToAdj(current, dir));
                        }
                    }
                }
            }
        }
    }

	/**
	 * Increase the traffic-density measurement at a particular
	 * spot.
	 * @param traffic the amount to add to the density
	 */
	void addTraffic(int mapX, int mapY, int traffic)
	{
		int z = trfDensity[mapY][mapX];
		z += PRNG.nextInt(rd(traffic,4))+rd(3*traffic,4); //1/4 random, 3/4 without random

		if (z > 480)
		{
			z = 480;
			trafficMaxLocationX = mapX;
			trafficMaxLocationY = mapY;
			if (PRNG.nextInt(6) == 0) {
				HelicopterSprite copter = (HelicopterSprite) getSprite(SpriteKind.COP);
				if (copter != null) {
					copter.destX = mapX;
					copter.destY = mapY;
				}
			}
		}

		trfDensity[mapY][mapX] = z;
	}
	
	public int rd(int val, int rd) {
		int ret=val/rd;
		if (PRNG.nextInt(rd)<val%rd) {
			ret++;
		}
		return ret;
	}

	/** Accessor method for fireRate[]. */
	public int getFireStationCoverage(int xpos, int ypos)
	{
		return fireRate[ypos][xpos];
	}

	/** Accessor method for landValueMem overlay. */
	public int getLandValue(int xpos, int ypos)
	{
		if (testBounds(xpos, ypos)) {
			return landValueMem[ypos][xpos];
		}
		else {
			return 0;
		}
	}

	public int getTrafficDensity(int xpos, int ypos)
	{
		if (testBounds(xpos, ypos)) {
			return this.trfMem[ypos][xpos]/4;
		} else {
			return 0;
		}
	}

	//power, terrain, land value
    public void pollutionScan(){
        {



            int pcount = 0;
            int ptotal = 0;
            int pmax = 0;

            final int HWLDX = (getWidth());
            final int HWLDY = (getHeight());
            for (int x = 0; x < HWLDX; x++)
            {
                for (int y = 0; y < HWLDY; y++)
                {
                    int tile = getTile(x, y);
                    int curPollution = (int)  ((double) (getPollutionValue(tile) * 2)-20*Math.sqrt((double) pollutionsccount));
                    pollutionMem[y][x] = curPollution;

                    if (curPollution != 0)
                    {
                        pcount++;
                        ptotal += curPollution;

                        if (curPollution > pmax ||
                                (curPollution == pmax && PRNG.nextInt(4) == 0))
                        {
                            pmax = curPollution;
                            pollutionMaxLocationX = x;
                            pollutionMaxLocationY = y;
                        }
                    }
                }
            }
            pollutionAverage = pcount != 0 ? (ptotal / pcount) : 0;

            pollutionMem = doSmooth(pollutionMem);
            spreadEffect(pollutionMem, 17, 8, 15);
        }
    }




    void ptlScan()
    {
        final int qX = (getWidth());
        final int qY = (getHeight());
        int [][] qtem = new int[qY][qX];

        int landValueTotal = 0;
        int landValueCount = 0;

        final int HWLDX = (getWidth());
        final int HWLDY = (getHeight());
        int [][] tem = new int[HWLDY][HWLDX];

        pollutionScan();

        for (int x = 0; x < HWLDX; x++)
        {
            for (int y = 0; y < HWLDY; y++)
            {
                int plevel = 0;
                int lvflag = 0;

                int tile = getTile(x, y);
                if (tile != DIRT)
                {
                    if (tile < RUBBLE) //natural land features
                    {
                        //inc terrainMem
                        qtem[y][x] += 15;
                        continue;
                    }

                    if (isConstructed(tile))
                        lvflag++;
                }

                if (lvflag != 0)
                {
                    //land value equation


                    // getDisCC should check for every city hall if it is in distance
                    int dis = 34 - getDisCC(x, y);
                    dis *= 6;
                    dis += terrainMem[y][x]*8;
                    dis -= pollutionMem[y][x]/8;
                    if (crimeMem[y][x] > 190) {
                        dis -= 20;
                    }
                    if (dis > 250)
                        dis = 250;
                    if (dis < 1)
                        dis = 1;


                    landValueMem[y][x] = dis;
                    landValueTotal += landValueMem[y][x];
                    landValueCount++;
                }
                else
                {
                    landValueMem[y][x] = 0;
                }

            }
        }

        landValueAverage = landValueCount != 0 ? (landValueTotal/landValueCount) : 0;




        terrainMem = smoothTerrain(qtem);

        fireMapOverlayDataChanged(MapState.POLLUTE_OVERLAY);   //PLMAP
        fireMapOverlayDataChanged(MapState.LANDVALUE_OVERLAY); //LVMAP
        fireMapOverlayDataChanged(MapState.VISIT_OVERLAY); //RGMAP
    }

	public CityLocation getLocationOfMaxPollution()
	{
		return new CityLocation(pollutionMaxLocationX, pollutionMaxLocationY);
	}
	
	//growth depending on tax
	static final int [] TaxTable = {
		200, 150, 120, 100, 80, 50, 30, 0, -10, -40, -100,
		-150, -200, -250, -300, -350, -400, -450, -500, -550, -600 };

	public static class History
	{
		public int cityTime;
		public int [] res = new int[240];
		public int [] com = new int[240];
		public int [] ind = new int[240];
		public int [] money = new int[240];
		public int [] pollution = new int[240];
		public int [] crime = new int[240];
        public int [] education = new int[240];
        public int [] culture = new int[240];
		int resMax;
		int comMax;
		int indMax;
	}
	public History history = new History();

	void setValves()
	{
		double normResPop = (double)resPop / 8.0;
		totalPop = (int) (normResPop + comPop + indPop);
		double employment;
		if (normResPop != 0.0)
		{
			employment = 2*(history.com[1] + history.ind[1]) / (normResPop+history.com[1] + history.ind[1]);
		}
		else
		{
			employment = 2;
		}

		double migration = normResPop * (employment - 1);
		final double BIRTH_RATE = 0.02;
		double births = (double)normResPop * BIRTH_RATE;
		double projectedResPop = normResPop + migration + births;

		// clamp laborBase to between 0.0 and 1.3
		employment = Math.max(0.0, Math.min(1.3, employment));

		int z = gameLevel;
		double temp = 1.0;
		switch (z)
		{
		case 0: temp = 1.2; break;
		case 1: temp = 1.1; break;
		case 2: temp = 1; break;
		}

		double resRatio;
		if (normResPop != 0) {
			resRatio = (double)projectedResPop / (double)normResPop;
		}
		else {
			resRatio = 1.3;
		}

		double comRatio;
		if (comPop != 0)
			comRatio = 2*employment*(2-employment);
		else
			comRatio = 2*employment*(2-employment);
		if (comRatio>0) {
			comRatio*=temp;
		} else {
			comRatio/=temp;
		}

		double indRatio;
		if (indPop != 0)
			indRatio = (2*temp-employment);
		else
			indRatio = (temp*2-employment);

		if (resRatio > 2.0)
			resRatio = 2.0;

		if (comRatio > 2.0)
			comRatio = 2.0;

		if (indRatio > 2.0)
			indRatio = 2.0;
		
		resRatio = (resRatio - 1) * 600+100;
		comRatio = (comRatio - 1) * 600;
		indRatio = (indRatio - 1) * 600;

		// ratios are velocity changes to valves
		resValve += (int) resRatio;
		comValve += (int) comRatio;
		indValve += (int) indRatio;

		if (resValve > 2000)
			resValve = 2000;
		else if (resValve < -2000)
			resValve = -2000;

		if (comValve > 1500)
			comValve = 1500;
		else if (comValve < -1500)
			comValve = -1500;

		if (indValve > 1500)
			indValve = 1500;
		else if (indValve < -1500)
			indValve = -1500;

		if (comCap && comValve > 0) {
			// commerce demands airport
			comValve = 0;
		}

		if (indCap && indValve > 0) {
			// industry demands sea port
			indValve = 0;
		}

		fireDemandChanged();
	}

	int [][] smoothTerrain(int [][] qtem)
	{
		final int QWX = qtem[0].length;
		final int QWY = qtem.length;

		int [][] mem = new int[QWY][QWX];
		for (int y = 0; y < QWY; y++)
		{
			for (int x = 0; x < QWX; x++)
			{
				int z = 0;
				if (x > 0)
					z += qtem[y][x-1];
				if (x+1 < QWX)
					z += qtem[y][x+1];
				if (y > 0)
					z += qtem[y-1][x];
				if (y+1 < QWY)
					z += qtem[y+1][x];
				mem[y][x] = z / 4 + qtem[y][x] / 2;
			}
		}
		return mem;
	}

    int valueMapping(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    double valueMapping(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }



	// calculate manhatten distance from center of city
	// capped at 32
	//FIX Why do cap at 32? Consider to increase the cap.
	int getDisCC(int x, int y)
	{
		assert x >= 0 && x <= getWidth();
		assert y >= 0 && y <= getHeight();
        int xdis = Math.abs(x - centerMassX);
        int ydis = Math.abs(y - centerMassY);
        int centerMassDistance = 10+(xdis+ydis)/2;
        int ccDis;

        if(centerMassDistance > 32) centerMassDistance = 32;

        int closestDistance = 32;
        //alternatively adding bonus if there are two cityHalls near
        // also alternatively bonusing centerMass as original

        if(cityHallList.size() > 0){
        //getting the distance to the closest cityHall
            for(CityLocation cityHallLocation : cityHallList){
              int cur_xdis = Math.abs(x - cityHallLocation.x);
              int cur_ydis = Math.abs(y - cityHallLocation.y);
             int curDistance = (cur_xdis + cur_ydis);
             if(curDistance < closestDistance) closestDistance = curDistance;
           }
        } else {
            if(centerMassDistance < 32) closestDistance = centerMassDistance;
        }
        ccDis = closestDistance;

        // also some bonus if tile is close to the centerMass (the actual cityccenter by mass)
        // it has only 1/4 effect than previously though
        int centerMassDistanceB = centerMassDistance * 4; //making the centerMassDistance 4 times bigger so it has lesser effect
        centerMassDistanceB = clamp(centerMassDistanceB, 0,32);
        int bonusValue = valueMapping(centerMassDistanceB, 1,32, 32,0); // if centerMassDistance is 1 (close) then the bonous is big
        ccDis = clamp((closestDistance - bonusValue), 1,32);

		return ccDis;
	}

	Map<String,TileBehavior> tileBehaviors;
	void initTileBehaviors()
	{
		HashMap<String,TileBehavior> bb;
		bb = new HashMap<String,TileBehavior>();

		bb.put("FIRE", new TerrainBehavior(this, TerrainBehavior.B.FIRE));
		bb.put("FLOOD", new TerrainBehavior(this, TerrainBehavior.B.FLOOD));
		bb.put("RADIOACTIVE", new TerrainBehavior(this, TerrainBehavior.B.RADIOACTIVE));
		bb.put("ROAD", new TerrainBehavior(this, TerrainBehavior.B.ROAD));
		bb.put("BIGROAD", new TerrainBehavior(this, TerrainBehavior.B.BIGROAD));
		bb.put("RAIL", new TerrainBehavior(this, TerrainBehavior.B.RAIL));
		bb.put("STATION", new TerrainBehavior(this, TerrainBehavior.B.STATION));
		bb.put("EXPLOSION", new TerrainBehavior(this, TerrainBehavior.B.EXPLOSION));
		bb.put("RESIDENTIAL", new MapScanner(this, MapScanner.B.RESIDENTIAL));
		bb.put("HOSPITAL_CHURCH", new MapScanner(this, MapScanner.B.HOSPITAL_CHURCH));
		bb.put("COMMERCIAL", new MapScanner(this, MapScanner.B.COMMERCIAL));
		bb.put("INDUSTRIAL", new MapScanner(this, MapScanner.B.INDUSTRIAL));
		bb.put("COAL", new MapScanner(this, MapScanner.B.COAL));
		bb.put("NUCLEAR", new MapScanner(this, MapScanner.B.NUCLEAR));
		bb.put("FIRESTATION", new MapScanner(this, MapScanner.B.FIRESTATION));
		bb.put("POLICESTATION", new MapScanner(this, MapScanner.B.POLICESTATION));
		bb.put("SCHOOLBUILDING", new MapScanner(this, MapScanner.B.SCHOOLBUILDING));
		bb.put("MUSEUMBUILDING", new MapScanner(this, MapScanner.B.MUSEUMBUILDING));
		bb.put("UNIABUILDING", new MapScanner(this, MapScanner.B.UNIABUILDING));
		bb.put("UNIBBUILDING", new MapScanner(this, MapScanner.B.UNIBBUILDING));
		bb.put("OPENAIRBUILDING", new MapScanner(this, MapScanner.B.OPENAIRBUILDING));
		bb.put("CITYHALLBUILDING", new MapScanner(this, MapScanner.B.CITYHALLBUILDING));
		bb.put("STADIUM_EMPTY", new MapScanner(this, MapScanner.B.STADIUM_EMPTY));
		bb.put("STADIUM_FULL", new MapScanner(this, MapScanner.B.STADIUM_FULL));
		bb.put("AIRPORT", new MapScanner(this, MapScanner.B.AIRPORT));
		bb.put("SEAPORT", new MapScanner(this, MapScanner.B.SEAPORT));
		bb.put("BIGPARKBUILDING", new MapScanner(this, MapScanner.B.BIGPARKBUILDING));
		bb.put("SOLAR", new MapScanner(this, MapScanner.B.SOLAR));
		bb.put("WIND", new MapScanner(this, MapScanner.B.WIND));
		this.tileBehaviors = bb;
	}

	void mapScan(int x0, int x1)
	{
		for (int x = x0; x < x1; x++)
		{
			for (int y = 0; y < getHeight(); y++)
			{
				mapScanTile(x, y);
			}
		}
	}

	void mapScanTile(int xpos, int ypos)
	{
		int tile = getTile(xpos, ypos);
		String behaviorStr = getTileBehavior(tile);
		if (behaviorStr == null) {
			return; //nothing to do
		}
		
		TileBehavior b = tileBehaviors.get(behaviorStr);
		if (b != null) {
            if (TileConstants.isZoneCenter(tile)) {
                visitNew.put(new CityLocation(xpos,ypos),0);
            }
            b.processTile(xpos, ypos);
		}
		else {
			throw new Error("Unknown behavior: "+behaviorStr);
		}
	}

    void spendTechnologyPoints(){
        if(this.selectedEETech != null && this.technologyEEPoints != 0.0){
            this.selectedEETech.addResearchPoints(this.technologyEEPoints);

            // if a building technology got researched reset the selection
            if(this.selectedEETech.getIsResearched() == true && (this.selectedEETech instanceof BuildingTechnology)){
                this.selectedEETech = null;
            }
            this.technologyEEPoints = 0;
        }

        if(this.selectedInfraTech != null && this.technologyInfraPoints != 0.0){
            this.selectedInfraTech.addResearchPoints(this.technologyInfraPoints);

            // if a building technology got researched reset the selection
            if(this.selectedInfraTech.getIsResearched() == true && (this.selectedEETech instanceof BuildingTechnology)){
                this.selectedInfraTech = null;
            }
            technologyInfraPoints = 0;
        }
    }
    
    public void setSelectedEETech(GeneralTechnology t){
        if(this.selectedEETech != null){
            // reset previous research
            this.selectedEETech.resetResearchPoints();
        }
    	this.selectedEETech = t;
    }
    
    public void setSelectInfraTech(GeneralTechnology t){
        if(this.selectedInfraTech != null){
            // reset previous research
            this.selectedInfraTech.resetResearchPoints();
        }
    	this.selectedInfraTech = t;
    }

	void generateShip()
	{
		int edge = PRNG.nextInt(4);

		if (edge == 0) {
			for (int x = 4; x < getWidth() - 2; x++) {
				if (getTile(x,0) == CHANNEL) {
					makeShipAt(x, 0, ShipSprite.NORTH_EDGE);
					return;
				}
			}
		}
		else if (edge == 1) {
			for (int y = 1; y < getHeight() - 2; y++) {
				if (getTile(0,y) == CHANNEL) {
					makeShipAt(0, y, ShipSprite.EAST_EDGE);
					return;
				}
			}
		}
		else if (edge == 2) {
			for (int x = 4; x < getWidth() - 2; x++) {
				if (getTile(x, getHeight()-1) == CHANNEL) {
					makeShipAt(x, getHeight()-1, ShipSprite.SOUTH_EDGE);
					return;
				}
			}
		}
		else {
			for (int y = 1; y < getHeight() - 2; y++) {
				if (getTile(getWidth()-1, y) == CHANNEL) {
					makeShipAt(getWidth()-1, y, ShipSprite.EAST_EDGE);
					return;
				}
			}
		}
	}

	Sprite getSprite(SpriteKind kind)
	{
		for (Sprite s : sprites) {
			if (s.kind == kind)
				return s;
		}
		return null;
	}

	boolean hasSprite(SpriteKind kind)
	{
		return getSprite(kind) != null;
	}

	void makeShipAt(int xpos, int ypos, int edge)
	{
		assert !hasSprite(SpriteKind.SHI);

		sprites.add(new ShipSprite(this, xpos, ypos, edge));
	}

	void generateCopter(int xpos, int ypos)
	{
		if (!hasSprite(SpriteKind.COP)) {
			sprites.add(new HelicopterSprite(this, xpos, ypos));
		}
	}

	void generatePlane(int xpos, int ypos)
	{
		if (!hasSprite(SpriteKind.AIR)) {
			sprites.add(new AirplaneSprite(this, xpos, ypos));
		}
	}

	void generateTrain(int xpos, int ypos)
	{
		if (totalPop > 20 &&
			!hasSprite(SpriteKind.TRA) &&
			PRNG.nextInt(26) == 0)
		{
			sprites.add(new TrainSprite(this, xpos, ypos));
		}
	}

	Stack<CityLocation> powerPlants = new Stack<CityLocation>();

	// counts the population in a certain type of residential zone
	int doFreePop(int xpos, int ypos) {
		int count = 0;

		for (int x = xpos - 1; x <= xpos + 1; x++)
		{
			for (int y = ypos - 1; y <= ypos + 1; y++)
			{
				if (testBounds(x,y))
				{
					char loc = getTile(x, y);
					if (loc >= LHTHR && loc <= HHTHR)
						count++;
				}
			}
		}
		return count;
	}

	// called every several cycles; this takes the census data collected in this
	// cycle and records it to the history
	//
	void takeCensus()
	{
		int resMax = 0;
		int comMax = 0;
		int indMax = 0;

        // inertia for education and cultur  
        educationValue *= 5.0/8.0;
        cultureValue *= 5.0/8.0;
        updateEducationAverage(1);
        updateCultureAverage(1);


		for (int i = 118; i >= 0; i--)
		{
			if (history.res[i] > resMax)
				resMax = history.res[i];
			if (history.com[i] > comMax)
				comMax = history.res[i];
			if (history.ind[i] > indMax)
				indMax = history.ind[i];

			history.res[i + 1] = history.res[i];
			history.com[i + 1] = history.com[i];
			history.ind[i + 1] = history.ind[i];
			history.crime[i + 1] = history.crime[i];
            history.education[i + 1] = history.education[i];
            history.culture[i + 1] = history.culture[i];
			history.pollution[i + 1] = history.pollution[i];
			history.money[i + 1] = history.money[i];
		}

		history.resMax = resMax;
		history.comMax = comMax;
		history.indMax = indMax;

		//graph10max = Math.max(resMax, Math.max(comMax, indMax));

		history.res[0] = resPop / 8;
		history.com[0] = comPop;
		history.ind[0] = indPop;

		crimeRamp = (crimeAverage);
		history.crime[0] = Math.min(255, crimeRamp);

		polluteRamp = pollutionAverage;
		history.pollution[0] = Math.min(255, polluteRamp);

        educationRamp = (educationAverage);
        history.education[0] = Math.min(255, educationRamp);

        history.culture[0] = Math.min(255, cultureAverage);

		int moneyScaled = cashFlow / 20 + 128;
		if (moneyScaled < 0)
			moneyScaled = 0;
		if (moneyScaled > 255)
			moneyScaled = 255;
		history.money[0] = moneyScaled;

		history.cityTime = cityTime;

		if (hospitalCount < resPop / 256)
		{
			needHospital = 1;
		}
		else if (hospitalCount > resPop / 256)
		{
			needHospital = -1;
		}
		else
		{
			needHospital = 0;
		}

		if (churchCount < resPop / 256)
		{
			needChurch = 1;
		}
		else if (churchCount > resPop / 256)
		{
			needChurch = -1;
		}
		else
		{
			needChurch = 0;
		}
	}

	void takeCensus2()
	{
		// update long term graphs
		int resMax = 0;
		int comMax = 0;
		int indMax = 0;

		for (int i = 238; i >= 120; i--)
		{
			if (history.res[i] > resMax)
				resMax = history.res[i];
			if (history.com[i] > comMax)
				comMax = history.res[i];
			if (history.ind[i] > indMax)
				indMax = history.ind[i];

			history.res[i + 1] = history.res[i];
			history.com[i + 1] = history.com[i];
			history.ind[i + 1] = history.ind[i];
			history.crime[i + 1] = history.crime[i];
            history.education[i + 1] = history.education[i];
            history.culture[i + 1] = history.culture[i];
			history.pollution[i + 1] = history.pollution[i];
			history.money[i + 1] = history.money[i];
		}

		history.res[120] = resPop / 8;
		history.com[120] = comPop;
		history.ind[120] = indPop;
		history.crime[120] = history.crime[0];
        history.education[120] = history.education[0];
        history.culture[120] = history.culture[0];
		history.pollution[120] = history.pollution[0];
		history.money[120] = history.money[0];
	}

	/** Road/rail maintenance cost multiplier, for various difficulty settings.
	 */
	static final double [] RLevels = { 0.7, 0.9, 1.2 };

	//tax income
	/** Tax income multiplier, for various difficulty settings.
	 */
	static final double [] FLevels = { 2.1, 1.8, 1.2 };

	void collectTaxPartial()
	{
		lastRoadTotal = roadTotal;
		lastBigRoadTotal = bigroadTotal;
		lastRailTotal = railTotal;
		lastStationTotal = stationTotal;
		lastTotalPop = totalPop;
		lastFireStationCount = fireStationCount;
		lastPoliceCount = policeCount;
		lastSchoolCount = schoolCount;
		lastMuseumCount = museumCount;
		lastUniACount = uniaCount;
		lastUniBCount = unibCount;
		lastCityHallCount = cityhallCount;
		lastOpenAirCount = openairCount;
		lastStadiumCount = stadiumCount;

		BudgetNumbers b = generateBudget();

		budget.taxFund += b.taxIncome;
		budget.roadFundEscrow -= b.roadFunded;
		budget.fireFundEscrow -= b.fireFunded;
		budget.policeFundEscrow -= b.policeFunded;
		budget.schoolFundEscrow -= b.schoolFunded;
		budget.cultureFundEscrow -= b.cultureFunded;
		
		taxEffect = b.taxRate;
		roadEffect = b.roadRequest != 0 ?
			(int)Math.floor(32.0 * (double)b.roadFunded / (double)b.roadRequest) :
			32;
		policeEffect = b.policeRequest != 0 ?
			(int)Math.floor(10000.0 * (double)b.policeFunded / (double)b.policeRequest) :
			1000;
		fireEffect = b.fireRequest != 0 ?
			(int)Math.floor(1000.0 * (double)b.fireFunded / (double)b.fireRequest) :
			1000;
		educationEffect = b.schoolRequest != 0 ?
			(int)Math.floor(1000.0 * (double)b.schoolFunded / (double)b.schoolRequest) :
			1000;
		cultureEffect = b.cultureRequest != 0 ?
					(int)Math.floor(1000.0 * (double)b.cultureFunded / (double)b.cultureRequest) :
					1000;
	}

	public static class FinancialHistory
	{
		public int cityTime;
		public int totalFunds;
		public int taxIncome;
		public int operatingExpenses;
	}
	public ArrayList<FinancialHistory> financialHistory = new ArrayList<FinancialHistory>();

	void collectTax()
	{
		int revenue = budget.taxFund / TAXFREQ;
		int expenses = -(budget.roadFundEscrow + budget.fireFundEscrow + budget.policeFundEscrow + budget.schoolFundEscrow
			+ budget.cultureFundEscrow)/ TAXFREQ;

		FinancialHistory hist = new FinancialHistory();
		hist.cityTime = cityTime;
		hist.taxIncome = revenue;
		hist.operatingExpenses = expenses;

		cashFlow = revenue - expenses;
		spend(-cashFlow);

		hist.totalFunds = budget.totalFunds;
		financialHistory.add(0,hist);

		budget.taxFund = 0;
		budget.roadFundEscrow = 0;
		budget.fireFundEscrow = 0;
		budget.policeFundEscrow = 0;
		budget.schoolFundEscrow = 0;
		budget.cultureFundEscrow = 0;
	}

	/** Annual maintenance cost of each police station. */
	static final int POLICE_STATION_MAINTENANCE = 100;

	/** Annual maintenance cost of each fire station. */
	static final int FIRE_STATION_MAINTENANCE = 100;
	
	/** Annual maintenance cost of each police station. */
	static final int SCHOOL_MAINTENANCE = 100;
	
	static final int CULTURE_MAINTENANCE = 100;


	/**
	 * Calculate the current budget numbers.
	 */
	public BudgetNumbers generateBudget()
	{
		BudgetNumbers b = new BudgetNumbers();
		b.taxRate = Math.max(0, cityTax);
		b.roadPercent = Math.max(0.0, roadPercent);
		b.firePercent = Math.max(0.0, firePercent);
		b.policePercent = Math.max(0.0, policePercent);
		b.schoolPercent = Math.max(0.0, schoolPercent);
		b.culturePercent = Math.max(0.0, culturePercent);


		b.previousBalance = budget.totalFunds;
		//tax income
		b.taxIncome = (int)Math.round(lastTotalPop * landValueAverage / 120 * b.taxRate * FLevels[gameLevel]);
		assert b.taxIncome >= 0;

		b.roadRequest = (int)Math.round((lastRoadTotal + lastRailTotal * 2) * RLevels[gameLevel]);
		b.fireRequest = FIRE_STATION_MAINTENANCE * lastFireStationCount;
		b.policeRequest = POLICE_STATION_MAINTENANCE * lastPoliceCount;
		b.schoolRequest = SCHOOL_MAINTENANCE * (lastSchoolCount + lastUniACount + lastUniBCount);
		b.cultureRequest = CULTURE_MAINTENANCE * (lastMuseumCount + lastStadiumCount + lastOpenAirCount); 
		
		b.roadFunded = (int)Math.round(b.roadRequest * b.roadPercent);
		b.fireFunded = (int)Math.round(b.fireRequest * b.firePercent);
		b.policeFunded = (int)Math.round(b.policeRequest * b.policePercent);
		b.schoolFunded = (int)Math.round(b.schoolRequest * b.schoolPercent);
		b.cultureFunded = (int)Math.round(b.cultureRequest * b.culturePercent);
		
		int yumDuckets = budget.totalFunds + b.taxIncome;
		assert yumDuckets >= 0;

		// calculating budget. subtracting all the costs and checking if it still can be subtracted
		// definetly need to refactor here
		if (yumDuckets >= b.roadFunded)
		{
			yumDuckets -= b.roadFunded;
			if (yumDuckets >= b.fireFunded)
			{
				yumDuckets -= b.fireFunded;
				if (yumDuckets >= b.policeFunded)
				{
					yumDuckets -= b.policeFunded;
					if (yumDuckets >= b.schoolFunded)
					{
						yumDuckets -= b.schoolFunded;
						if (yumDuckets >= b.cultureFunded)
						{
							yumDuckets -= b.cultureFunded;
						}
						else
						{
							assert b.cultureRequest !=0;
							
							b.cultureFunded = yumDuckets;
							b.culturePercent = (double)b.cultureFunded / (double)b.cultureRequest;
							yumDuckets = 0;
						}
					}
					else
					{
						assert b.schoolRequest != 0;

						b.schoolFunded = yumDuckets;
						b.schoolPercent = (double)b.schoolFunded / (double)b.schoolRequest;
						b.cultureFunded = 0;
						b.culturePercent = 0.0;
						yumDuckets = 0;
						
					}
				}
				else
				{
					assert b.schoolRequest != 0;

					b.policeFunded = yumDuckets;
					b.policePercent = (double)b.policeFunded / (double)b.policeRequest;
					b.schoolFunded = 0;
					b.schoolPercent = 0.0;
					b.cultureFunded = 0;
					b.culturePercent = 0.0;
					yumDuckets = 0;
				}
			}
			else
			{
				assert b.fireRequest != 0;

				b.fireFunded = yumDuckets;
				b.firePercent = (double)b.fireFunded / (double)b.fireRequest;
				b.policeFunded = 0;
				b.policePercent = 0.0;
				b.schoolFunded = 0;
				b.schoolPercent = 0.0;
				b.cultureFunded = 0;
				b.culturePercent = 0.0;
				yumDuckets = 0;
			}
		}
		else
		{
			assert b.roadRequest != 0;

			b.roadFunded = yumDuckets;
			b.roadPercent = (double)b.roadFunded / (double)b.roadRequest;
			b.fireFunded = 0;
			b.firePercent = 0.0;
			b.policeFunded = 0;
			b.policePercent = 0.0;
			b.schoolFunded = 0;
			b.schoolPercent = 0.0;
			b.cultureFunded = 0;
			b.culturePercent = 0.0;
		}

		b.operatingExpenses = b.roadFunded + b.fireFunded + b.policeFunded + b.schoolFunded + b.cultureFunded;
		b.newBalance = b.previousBalance + b.taxIncome - b.operatingExpenses;

		return b;
	}

	int getPopulationDensity(int xpos, int ypos)
	{
		return popDensity[ypos][xpos];
	}

	void doMeltdown(int xpos, int ypos)
	{
		meltdownLocation = new CityLocation(xpos, ypos);

		makeExplosion(xpos - 1, ypos - 1);
		makeExplosion(xpos - 1, ypos + 2);
		makeExplosion(xpos + 2, ypos - 1);
		makeExplosion(xpos + 2, ypos + 2);

		for (int x = xpos - 1; x < xpos + 3; x++) {
			for (int y = ypos - 1; y < ypos + 3; y++) {
				setTile(x, y, (char)(FIRE + PRNG.nextInt(4)));
			}
		}

		for (int z = 0; z < 200; z++) {
			int x = xpos - 20 + PRNG.nextInt(41);
			int y = ypos - 15 + PRNG.nextInt(31);
			if (!testBounds(x,y))
				continue;

			int t = map[y][x];
			if (isZoneCenter(t)) {
				continue;
			}
			if (isCombustible(t) || t == DIRT) {
				setTile(x, y, RADTILE);
			}
		}

		clearMes();
		sendMessageAt(MicropolisMessage.MELTDOWN_REPORT, xpos, ypos);
	}

	static final int [] MltdwnTab = { 30000, 20000, 10000 };

	void loadHistoryArray(int [] array, DataInputStream dis)
		throws IOException
	{
		for (int i = 0; i < 240; i++)
		{
			array[i] = dis.readShort();
		}
	}

	void writeHistoryArray(int [] array, DataOutputStream out)
		throws IOException
	{
		for (int i = 0; i < 240; i++)
		{
			out.writeShort(array[i]);
		}
	}

	void loadMisc(DataInputStream dis)
		throws IOException
	{
		dis.readShort(); //[0]... ignored?
		dis.readShort(); //[1] externalMarket, ignored
		resPop = dis.readShort();  //[2-4] populations
		comPop = dis.readShort();
		indPop = dis.readShort();
		resValve = dis.readShort(); //[5-7] valves
		comValve = dis.readShort();
		indValve = dis.readShort();
		cityTime = dis.readInt();   //[8-9] city time
		crimeRamp = dis.readShort(); //[10]
		polluteRamp = dis.readShort();
		landValueAverage = dis.readShort(); //[12]
		crimeAverage = dis.readShort();
		pollutionAverage = dis.readShort(); //[14]
		gameLevel = dis.readShort();
		evaluation.cityClass = dis.readShort();  //[16]
		evaluation.cityScore = dis.readShort();

		for (int i = 18; i < 50; i++)
		{
			dis.readShort();
		}

		budget.totalFunds = dis.readInt();   //[50-51] total funds
		autoBulldoze = dis.readShort() != 0;    //52
		autoBudget = dis.readShort() != 0;
		autoGo = dis.readShort() != 0;          //54
		dis.readShort();  // userSoundOn (this setting not saved to game file
				// in this edition of the game)
		cityTax = dis.readShort();              //56
		taxEffect = cityTax;
		int simSpeedAsInt = dis.readShort();
		if (simSpeedAsInt >= 0 && simSpeedAsInt <= 4)
			simSpeed = Speed.values()[simSpeedAsInt];
		else
			simSpeed = Speed.NORMAL;

		// read budget numbers, convert them to percentages
		//
		long n = dis.readInt();	               //58,59... police percent
		policePercent = (double)n / 65536.0;
		n = dis.readInt();                     //60,61... fire percent
		firePercent = (double)n / 65536.0;
		n = dis.readInt();                     //62,63... road percent
		roadPercent = (double)n / 65536.0;

		for (int i = 64; i < 120; i++)
		{
			dis.readShort();
		}

		if (cityTime < 0) { cityTime = 0; }
		if (cityTax < 0 || cityTax > 20) { cityTax = 7; }
		if (gameLevel < 0 || gameLevel > 2) { gameLevel = 0; }
		if (evaluation.cityClass < 0 || evaluation.cityClass > 5) { evaluation.cityClass = 0; }
		if (evaluation.cityScore < 1 || evaluation.cityScore > 999) { evaluation.cityScore = 500; }

		resCap = false;
		comCap = false;
		indCap = false;
	}

	void writeMisc(DataOutputStream out)
		throws IOException
	{
		out.writeShort(0);
		out.writeShort(0);
		out.writeShort(resPop);
		out.writeShort(comPop);
		out.writeShort(indPop);
		out.writeShort(resValve);
		out.writeShort(comValve);
		out.writeShort(indValve);
		//8
		out.writeInt(cityTime);
		out.writeShort(crimeRamp);
		out.writeShort(polluteRamp);
		//12
		out.writeShort(landValueAverage);
		out.writeShort(crimeAverage);
		out.writeShort(pollutionAverage);
		out.writeShort(gameLevel);
		//16
		out.writeShort(evaluation.cityClass);
		out.writeShort(evaluation.cityScore);
		//18
		for (int i = 18; i < 50; i++) {
			out.writeShort(0);
		}
		//50
		out.writeInt(budget.totalFunds);
		out.writeShort(autoBulldoze ? 1 : 0);
		out.writeShort(autoBudget ? 1 : 0);
		//54
		out.writeShort(autoGo ? 1 : 0);
		out.writeShort(1); //userSoundOn
		out.writeShort(cityTax);
		out.writeShort(simSpeed.ordinal());

		//58
		out.writeInt((int)(policePercent * 65536));
		out.writeInt((int)(firePercent * 65536));
		out.writeInt((int)(roadPercent * 65536));

		//64
		for (int i = 64; i < 120; i++) {
			out.writeShort(0);
		}
	}

	void loadMap(DataInputStream dis)
		throws IOException
	{
		for (int x = 0; x < DEFAULT_WIDTH; x++)
		{
			for (int y = 0; y < DEFAULT_HEIGHT; y++)
			{
				int z = dis.readShort();
				z &= ~(1024 | 2048 | 4096 | 8192 | 16384); // clear ZONEBIT,ANIMBIT,BULLBIT,BURNBIT,CONDBIT on import
				map[y][x] = (char) z;
			}
		}
	}

	void writeMap(DataOutputStream out)
		throws IOException
	{
		for (int x = 0; x < DEFAULT_WIDTH; x++)
		{
			for (int y = 0; y < DEFAULT_HEIGHT; y++)
			{
				int z = map[y][x];
				if (isConductive(z & LOMASK)) {
					z |= 16384;  //synthesize CONDBIT on export
				}
				if (isCombustible(z & LOMASK)) {
					z |= 8192;   //synthesize BURNBIT on export
				}
				if (isTileDozeable(x, y)) {
					z |= 4096;   //synthesize BULLBIT on export
				}
				if (isAnimated(z & LOMASK)) {
					z |= 2048;   //synthesize ANIMBIT on export
				}
				if (isZoneCenter(z & LOMASK)) {
					z |= 1024;   //synthesize ZONEBIT
				}
				out.writeShort(z);
			}
		}
	}

	public void load(File filename)
		throws IOException
	{
		FileInputStream fis = new FileInputStream(filename);
		if (fis.getChannel().size() > 27120) {
			// some editions of the classic Simcity game
			// start the file off with a 128-byte header,
			// but otherwise use the same format as us,
			// so read in that 128-byte header and continue
			// as before.
			byte [] bbHeader = new byte[128];
			fis.read(bbHeader);
		}
		load(fis);
	}

	void checkPowerMap()
	{
		coalCount = 0;
		nuclearCount = 0;

		powerPlants.clear();
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				int tile = getTile(x,y);
				if (tile == NUCLEAR) {
					nuclearCount++;
					powerPlants.add(new CityLocation(x,y));
				}
				else if (tile == POWERPLANT) {
					coalCount++;
					powerPlants.add(new CityLocation(x,y));
				}
                else if (tile == WIND) {
                    windCount++;
                    powerPlants.add(new CityLocation(x,y));
                }
                else if (tile == SOLAR) {
                    solarCount++;
                    powerPlants.add(new CityLocation(x,y));
                }
			}
		}

		powerScan();
		newPower = true;
	}

	public void load(InputStream inStream)
		throws IOException
	{
		DataInputStream dis = new DataInputStream(inStream);
		loadHistoryArray(history.res, dis);
		loadHistoryArray(history.com, dis);
		loadHistoryArray(history.ind, dis);
		loadHistoryArray(history.crime, dis);
		loadHistoryArray(history.pollution, dis);
		loadHistoryArray(history.money, dis);
		loadMisc(dis);
		loadMap(dis);
		dis.close();

		checkPowerMap();

		fireWholeMapChanged();
		fireDemandChanged();
		fireFundsChanged();
        resetNCheats();
    }

    public void save(File filename)
		throws IOException
	{
		save(new FileOutputStream(filename));
	}

	public void save(OutputStream outStream)
		throws IOException
	{
		DataOutputStream out = new DataOutputStream(outStream);
		writeHistoryArray(history.res, out);
		writeHistoryArray(history.com, out);
		writeHistoryArray(history.ind, out);
		writeHistoryArray(history.crime, out);
		writeHistoryArray(history.pollution, out);
		writeHistoryArray(history.money, out);
		writeMisc(out);
		writeMap(out);
		out.close();
	}

	public void toggleAutoBudget()
	{
		autoBudget = !autoBudget;
		fireOptionsChanged();
	}

	public void toggleAutoBulldoze()
	{
		autoBulldoze = !autoBulldoze;
		fireOptionsChanged();
	}

	public void toggleDisasters()
	{
		noDisasters = !noDisasters;
		fireOptionsChanged();
	}

    public boolean isPaused() {
        return isPaused;
    }

	public void setSpeed(Speed newSpeed)
	{
        if (!isPaused)
            simSpeed = newSpeed;
        oldSpeed = newSpeed;
        fireOptionsChanged();
	}

    public void pauseUnpause() {
        if (!isPaused) {
            simSpeed = Speed.PAUSED;
            isPaused = true;

        } else {
            simSpeed = oldSpeed;
            isPaused = false;

        }
    }

	public void animate()
	{
		this.acycle = (this.acycle+1) % 960;
		if (this.acycle % 2 == 0) {
			step();
		}
		moveObjects();
		animateTiles();
	}

	public Sprite [] allSprites()
	{
		return sprites.toArray(new Sprite[0]);
	}

	void moveObjects()
	{
		for (Sprite sprite : allSprites())
		{
			sprite.move();

			if (sprite.frame == 0) {
				sprites.remove(sprite);
			}
		}
	}

	void animateTiles()
	{
		for (int y = 0; y < map.length; y++)
		{
			for (int x = 0; x < map[y].length; x++)
			{
				char tilevalue = map[y][x];
				TileSpec spec = Tiles.get(tilevalue & LOMASK);
				if (spec != null && spec.animNext != null) {
					int flags = tilevalue & ALLBITS;
					setTile(x, y, (char)
						(spec.animNext.tileNumber | flags)
						);
				}
			}
		}
	}

	public int getCityPopulation()
	{
		return lastCityPop;
	}

	void makeSound(int x, int y, Sound sound)
	{
		fireCitySound(sound, new CityLocation(x, y));
	}

	public void makeEarthquake()
	{
		makeSound(centerMassX, centerMassY, Sound.EXPLOSION_LOW);
		fireEarthquakeStarted();

		sendMessageAt(MicropolisMessage.EARTHQUAKE_REPORT, centerMassX, centerMassY);
		int time = PRNG.nextInt(701) + 300;
		for (int z = 0; z < time; z++) {
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			assert testBounds(x, y);

			if (isVulnerable(getTile(x, y))) {
				if (PRNG.nextInt(4) != 0) {
					setTile(x, y, (char)(RUBBLE + PRNG.nextInt(4)));
				} else {
					setTile(x, y, (char)(FIRE + PRNG.nextInt(8)));
				}
			}
		}
	}

	void setFire()
	{
		int x = PRNG.nextInt(getWidth());
		int y = PRNG.nextInt(getHeight());
		int t = getTile(x, y);

		if (isArsonable(t)) {
			setTile(x, y, (char)(FIRE + PRNG.nextInt(8)));
			crashLocation = new CityLocation(x, y);
			sendMessageAt(MicropolisMessage.FIRE_REPORT, x, y);
		}
	}

	public void makeFire()
	{
		// forty attempts at finding place to start fire
		for (int t = 0; t < 40; t++)
		{
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			int tile = getTile(x, y);
			if (!isZoneCenter(tile) && isCombustible(tile))
			{
				if (tile > 21 && (tile <= LASTZONE || (tile > NEWZONE && tile <= NEWLASTZONE))) {
					setTile(x, y, (char)(FIRE + PRNG.nextInt(8)));
					sendMessageAt(MicropolisMessage.FIRE_REPORT, x, y);
					return;
				}
			}
		}
	}

	/**
	 * Force a meltdown to occur.
	 * @return true if a metldown was initiated.
	 */
	public boolean makeMeltdown()
	{
		ArrayList<CityLocation> candidates = new ArrayList<CityLocation>();
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (getTile(x, y) == NUCLEAR) {
					candidates.add(new CityLocation(x,y));
				}
			}
		}

		if (candidates.isEmpty()) {
			// tell caller that no nuclear plants were found
			return false;
		}

		int i = PRNG.nextInt(candidates.size());
		CityLocation p = candidates.get(i);
		doMeltdown(p.x, p.y);
		return true;
	}

	public void makeMonster()
	{
		MonsterSprite monster = (MonsterSprite) getSprite(SpriteKind.GOD);
		if (monster != null) {
			// already have a monster in town
			monster.soundCount = 1;
			monster.count = 1000;
			monster.flag = false;
			monster.destX = pollutionMaxLocationX;
			monster.destY = pollutionMaxLocationY;
			return;
		}

		// try to find a suitable starting spot for monster

		for (int i = 0; i < 300; i++) {
			int x = PRNG.nextInt(getWidth() - 19) + 10;
			int y = PRNG.nextInt(getHeight() - 9) + 5;
			int t = getTile(x, y);
			if (t == RIVER) {
				makeMonsterAt(x, y);
				return;
			}
		}

		// no "nice" location found, just start in center of map then
		makeMonsterAt(getWidth(), getHeight());
	}

	void makeMonsterAt(int xpos, int ypos)
	{
		assert !hasSprite(SpriteKind.GOD);
		sprites.add(new MonsterSprite(this, xpos, ypos));
	}

	public void makeTornado()
	{
		TornadoSprite tornado = (TornadoSprite) getSprite(SpriteKind.TOR);
		if (tornado != null) {
			// already have a tornado, so extend the length of the
			// existing tornado
			tornado.count = 200;
			return;
		}

		//FIXME- this is not exactly like the original code
		int xpos = PRNG.nextInt(getWidth() - 19) + 10;
		int ypos = PRNG.nextInt(getHeight() - 19) + 10;
		sprites.add(new TornadoSprite(this, xpos, ypos));
		sendMessageAt(MicropolisMessage.TORNADO_REPORT, xpos, ypos);
	}

	public void makeFlood()
	{
		final int [] DX = { 0, 1, 0, -1 };
		final int [] DY = { -1, 0, 1, 0 };

		for (int z = 0; z < 300; z++) {
			int x = PRNG.nextInt(getWidth());
			int y = PRNG.nextInt(getHeight());
			int tile = getTile(x, y);
			if (isRiverEdge(tile))
			{
				for (int t = 0; t < 4; t++) {
					int xx = x + DX[t];
					int yy = y + DY[t];
					if (testBounds(xx,yy)) {
						int c = map[yy][xx];
						if (isFloodable(c)) {
							setTile(xx, yy, FLOOD);
							floodCnt = 30;
							sendMessageAt(MicropolisMessage.FLOOD_REPORT, xx, yy);
							floodX = xx;
							floodY = yy;
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * Makes all component tiles of a zone bulldozable.
	 * Should be called whenever the key zone tile of a zone is destroyed,
	 * since otherwise the user would no longer have a way of destroying
	 * the zone.
	 * @see #shutdownZone
	 */
	void killZone(int xpos, int ypos, int zoneTile)
	{
		rateOGMem[ypos][xpos] -= 20;

		assert isZoneCenter(zoneTile);
		CityDimension dim = getZoneSizeFor(zoneTile);
		assert dim != null;
		assert dim.width >= 3;
		assert dim.height >= 3;

		int zoneBase = (zoneTile&LOMASK) - 1 - dim.width;

		// this will take care of stopping smoke animations
		shutdownZone(xpos, ypos, dim);
	}

	/**
	 * If a zone has a different image (animation) for when it is
	 * powered, switch to that different image here.
	 * Note: pollution is not accumulated here; see ptlScan()
	 * instead.
	 * @see #shutdownZone
	 */
	void powerZone(int xpos, int ypos, CityDimension zoneSize)
	{
		assert zoneSize.width >= 3;
		assert zoneSize.height >= 3;

		for (int dx = 0; dx < zoneSize.width; dx++) {
			for (int dy = 0; dy < zoneSize.height; dy++) {
				int x = xpos - 1 + dx;
				int y = ypos - 1 + dy;
				int tile = getTileRaw(x, y);
				TileSpec ts = Tiles.get(tile & LOMASK);
				if (ts != null && ts.onPower != null) {
					setTile(x, y,
					(char) (ts.onPower.tileNumber | (tile & ALLBITS))
					);
				}
			}
		}
	}

	/**
	 * If a zone has a different image (animation) for when it is
	 * powered, switch back to the original image.
	 * @see #powerZone
	 * @see #killZone
	 */
	void shutdownZone(int xpos, int ypos, CityDimension zoneSize)
	{
		assert zoneSize.width >= 3;
		assert zoneSize.height >= 3;

		for (int dx = 0; dx < zoneSize.width; dx++) {
			for (int dy = 0; dy < zoneSize.height; dy++) {
				int x = xpos - 1 + dx;
				int y = ypos - 1 + dy;
				int tile = getTileRaw(x, y);
				TileSpec ts = Tiles.get(tile & LOMASK);
				if (ts != null && ts.onShutdown != null) {
					setTile(x, y,
					(char) (ts.onShutdown.tileNumber | (tile & ALLBITS))
					);
				}
			}
		}
	}

	void makeExplosion(int xpos, int ypos)
	{
		makeExplosionAt(xpos * 16 + 8, ypos * 16 + 8);
	}

    void makeQuietExplosion(int xpos, int ypos) {
        makeQuietExplosionAt(xpos * 16 + 8, ypos * 16 + 8);
    }

    /**
	 * Uses x,y coordinates as 1/16th-length tiles.
	 */
	void makeExplosionAt(int x, int y)
	{
		sprites.add(new ExplosionSprite(this, x, y));
	}

    void makeQuietExplosionAt(int x, int y) {
        sprites.add(new QuietExplosionSprite(this, x, y));
    }

    void checkGrowth()
	{
		if (cityTime % 4 == 0) {
			int newPop = (resPop + comPop * 8 + indPop * 8) * 20;
			if (lastCityPop != 0) {
				MicropolisMessage z = null;
				if (lastCityPop < 500000 && newPop >= 500000) {
					z = MicropolisMessage.POP_500K_REACHED;
				} else if (lastCityPop < 100000 && newPop >= 100000) {
					z = MicropolisMessage.POP_100K_REACHED;
				} else if (lastCityPop < 50000 && newPop >= 50000) {
					z = MicropolisMessage.POP_50K_REACHED;
				} else if (lastCityPop < 10000 && newPop >= 10000) {
					z = MicropolisMessage.POP_10K_REACHED;
				} else if (lastCityPop < 2000 && newPop >= 2000) {
					z = MicropolisMessage.POP_2K_REACHED;
				}
				if (z != null) {
					sendMessage(z);
				}
			}
            lastCityPop = newPop + cheatedPopulation;
        }
	}

	void doMessages()
	{
		//MORE (scenario stuff)

		checkGrowth();

		int totalZoneCount = resZoneCount + comZoneCount + indZoneCount;
		int powerCount = nuclearCount + coalCount + windCount + solarCount;

		int z = cityTime % 64;
		switch (z) {
		case 1:
			if (totalZoneCount / 4 >= resZoneCount) {
				sendMessage(MicropolisMessage.NEED_RES);
			}
			break;
		case 5:
			if (totalZoneCount / 8 >= comZoneCount) {
				sendMessage(MicropolisMessage.NEED_COM);
			}
			break;
		case 10:
			if (totalZoneCount / 8 >= indZoneCount) {
				sendMessage(MicropolisMessage.NEED_IND);
			}
			break;
		case 14:
			if (100*noWay>getCityPopulation()) { //TODO balance
				sendMessage(MicropolisMessage.NEED_ROADS);
			}
			break;
		case 18:
			if (100*longWay>getCityPopulation()) { //TODO need to change
				sendMessage(MicropolisMessage.NEED_RAILS);
			}
			break;
		case 22:
			if (totalZoneCount > 10 && powerCount == 0) {
				sendMessage(MicropolisMessage.NEED_POWER);
			}
			break;
		case 26:
			resCap = (resPop > 500 && stadiumCount == 0);
			if (resCap) {
				sendMessage(MicropolisMessage.NEED_STADIUM);
			}
			break;
		case 28:
			indCap = (indPop > 70 && seaportCount == 0);
			if (indCap) {
				sendMessage(MicropolisMessage.NEED_SEAPORT);
			}
			break;
		case 30:
			comCap = (comPop > 100 && airportCount == 0);
			if (comCap) {
				sendMessage(MicropolisMessage.NEED_AIRPORT);
			}
			break;
		case 32:
			int TM = unpoweredZoneCount + poweredZoneCount;
			if (TM != 0) {
				if ((double)poweredZoneCount / (double)TM < 0.7) {
					sendMessage(MicropolisMessage.BLACKOUTS);
				}
			}
			break;
		case 35:
			if (pollutionAverage > 60) { // FIXME, consider changing threshold to 80
				sendMessage(MicropolisMessage.HIGH_POLLUTION);
			}
			break;
		case 42:
			if (crimeAverage > 100) {
				sendMessage(MicropolisMessage.HIGH_CRIME);
			}
			break;
		case 45:
			if (totalPop > 60 && fireStationCount == 0) {
				sendMessage(MicropolisMessage.NEED_FIRESTATION);
			}
			break;
		case 48:
			if (totalPop > 60 && policeCount == 0) {
				sendMessage(MicropolisMessage.NEED_POLICE);
			}
			break;
		case 51:
			if (cityTax > 12) {
				sendMessage(MicropolisMessage.HIGH_TAXES);
			}
			break;
        case 52:
            if (totalPop > 60 && schoolCount == 0) {
                sendMessage(MicropolisMessage.NEED_SCHOOL);
            }
            break;
		case 54:
			if (roadEffect < 20 && roadTotal > 30) {
				sendMessage(MicropolisMessage.ROADS_NEED_FUNDING);
			}
			break;
		case 57:
			if (fireEffect < 700 && totalPop > 20) {
				sendMessage(MicropolisMessage.FIRE_NEED_FUNDING);
			}
			break;
		case 60:
			if (policeEffect < 700 && totalPop > 20) {
				sendMessage(MicropolisMessage.POLICE_NEED_FUNDING);
			}
			break;
		case 63:
			if (trafficAverage > 60) {
				sendMessage(MicropolisMessage.HIGH_TRAFFIC);
			}
		case 67:
			if (educationEffect < 700 && totalPop > 20) {
				sendMessage(MicropolisMessage.SCHOOL_NEED_FUNDING);
			}
			break;
		case 70:
			if (cultureEffect < 700 && totalPop > 1000) {
				sendMessage(MicropolisMessage.CULTURE_NEED_FUNDING);
			}
			break;
		default:
			//nothing
		}
	}

	void clearMes()
	{
		//TODO.
		// What this does in the original code is clears the 'last message'
		// properties, ensuring that the next message will be delivered even
		// if it is a repeat.
	}

	public void sendMessage(MicropolisMessage message)
	{
		fireCityMessage(message, null);
	}

	public void sendMessageAt(MicropolisMessage message, int x, int y)
	{
		fireCityMessage(message, new CityLocation(x,y));
	}
	/*
	 * calculates accurate density for a 3x3 square
	 */
	
	public int getMapdata(int x,int y, String g) {
		int ret=0;
		int d=0;
		for (int xp=Math.max(0,x-1);xp<Math.min(x+2,getWidth());xp++) {
			for (int yp=Math.max(0,y-1);yp<Math.min(y+2,getHeight());xp++) {
				ret+=accessMap(x, y, g); 
				d++;
			}
		}
		//fix rounding, we should consider to round up
		 return ret/d;
	}
	public void noWay() {
		noWay++;
	}
	public void longWay() {
		longWay++;
	}
	
	private int accessMap(int x,int y, String g) { 
		if (g=="popDensity") {
			return popDensity[y][x];
		} else {
			if (g=="landValueMem") {
				return landValueMem[y][x];
			} else {
				if (g=="crimeMem") {
					return crimeMem[y][x];
				} else {
					if (g=="pollutionMem") {
						return pollutionMem[y][x];
					} else {
						if (g=="rateOGMem") {
							return rateOGMem[y][x];
						}
					}
				}
			}
		}
		return 0;
	}

	public ZoneStatus queryZoneStatus(int xpos, int ypos)
	{
		ZoneStatus zs = new ZoneStatus();
		zs.building = getDescriptionNumber(getTile(xpos, ypos));

        zs.location = new CityLocation(xpos, ypos);

		int z;
		z = (popDensity[ypos][xpos] / 64) % 4;
		zs.popDensity = z + 1;

		z = landValueMem[ypos][xpos];
		z = z < 30 ? 4 : z < 80 ? 5 : z < 150 ? 6 : 7;
		zs.landValue = z + 1;

		z = ((crimeMem[ypos][xpos] / 64) % 4) + 8;
		zs.crimeLevel = z + 1;

		z = Math.max(13,((pollutionMem[ypos][xpos] / 64) % 4) + 12);
		zs.pollution = z + 1;

		z = rateOGMem[ypos][xpos];
		z = z < 0 ? 16 : z == 0 ? 17 : z <= 100 ? 18 : 19;
		zs.growthRate = z + 1;

		return zs;
	}

	public int getResValve()
	{
		return resValve;
	}

	public int getComValve()
	{
		return comValve;
	}

	public int getIndValve()
	{
		return indValve;
	}

	public void setGameLevel(int newLevel)
	{
		assert GameLevel.isValid(newLevel);

		gameLevel = newLevel;
		fireOptionsChanged();
	}

	public void setFunds(int totalFunds)
	{
		budget.totalFunds = totalFunds;
	}
	/**
	 * Traffic costs to pass this field.
	 * @param cur: current road type.
	 * @return costs
	 */
	public int getTrafficCost(CityLocation loc, int cur) { //TODO test values
		char tile=getTile(loc.x,loc.y);
		if (TileConstants.isRoadAny(tile)) {
			if (TileConstants.isRoad(tile) && cur!=3) {
				return 70+trfDensity[loc.y][loc.x];
			}
			if (TileConstants.isBigRoad(tile) && cur!=3) {
				return 40+trfDensity[loc.y][loc.x];
			}
			if (TileConstants.isRail(tile) && cur>2) {
				return 30+trfDensity[loc.y][loc.x];
			}
		}
		return 999;
	}
	public int dummySearch(HashMap<CityLocation,Integer> map, CityLocation loc) {
		for (CityLocation tmp : map.keySet()) {
			if (CityLocation.equals(tmp,loc)) {
				return map.get(tmp);
			}
		}
		return 0;
	}
}
