package micropolisj.engine;

public class RoadSpecifiedTile {
	CityLocation loc;
	int roadType;
	public RoadSpecifiedTile (CityLocation Loc, int RoadType) {
		loc=Loc;
		roadType=RoadType;
	}
	public CityLocation getLocation() {
		return loc;
	}
	public int getRoadType() {
		return roadType;
	}
	public static boolean isRail(int roadType) {
		return roadType==3 ||roadType==4 || roadType==5 || roadType==6;
	}
	public static boolean isRoad(int roadType) {
		return roadType==1 || roadType==2 || roadType==4 || roadType==5 || roadType==6;
	}
}
