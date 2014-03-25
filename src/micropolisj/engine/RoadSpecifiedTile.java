package micropolisj.engine;
import java.lang.Comparable;

public class RoadSpecifiedTile implements Comparable<RoadSpecifiedTile>{
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
	public int hashCode(){
		return loc.x*330+loc.y*5+roadType;
	}
	public static boolean isRail(int roadType) {
		return roadType==3 ||roadType==4 || roadType==5 || roadType==6;
	}
	public static boolean isRoad(int roadType) {
		return roadType==1 || roadType==2 || roadType==4 || roadType==5 || roadType==6;
	}
	
	public static boolean equals(RoadSpecifiedTile a, RoadSpecifiedTile b){
		if(CityLocation.equals(a.loc, b.loc)&&a.roadType==b.roadType){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof RoadSpecifiedTile){
		RoadSpecifiedTile b = (RoadSpecifiedTile) o;
		if(this.loc.x==b.loc.x&&this.loc.y==b.loc.y&&this.roadType==b.roadType){
			return true;
		}else{return false;}}
			return false;
		
	}
	@Override
	public int compareTo(RoadSpecifiedTile b){
		if(this.loc.x==b.loc.x&&this.loc.y==b.loc.y&&this.roadType==b.roadType){
			return 0;
		}if(this.loc.x<b.loc.x){
			return -1;
		}if(this.loc.x>b.loc.x){
			return 1;
		}if(this.loc.y<b.loc.y){
			return -1;
		}if(this.loc.y>b.loc.y){
			return 1;
		}if(this.roadType<b.roadType){
			return -1;
		}
		return 1;
	}
}
