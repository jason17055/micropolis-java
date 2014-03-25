package micropolisj.engine;
import java.lang.Comparable;

public class VerySpecifiedTile implements Comparable<VerySpecifiedTile>{
	CityLocation loc;
	int roadType;
	int value;
	
	public VerySpecifiedTile(CityLocation loc, int roadType, int value){
		this.loc=loc;
		this.roadType=roadType;
		this.value=value;
	}
	public VerySpecifiedTile(RoadSpecifiedTile r, int value){
		this.loc=r.loc;
		this.roadType=r.roadType;
		this.value=value;
	}
	public boolean equals(Object o){
		if(o instanceof VerySpecifiedTile){
			VerySpecifiedTile v = (VerySpecifiedTile)o;
			return this.loc.x==v.loc.x&&this.loc.y==v.loc.y&&this.roadType==v.roadType&&this.value==v.value;
		}
		return false;
	}
	
	public int compareTo(VerySpecifiedTile b){
		if(this.equals(b)){
			return 0;
		}
		if(this.loc.x < b.loc.x){
			return -1;
		}
		if(this.loc.x > b.loc.x){
			return 1;
		}
		if(this.loc.y<b.loc.y){
			return -1;
		}
		if(this.loc.y>b.loc.y){
			return 1;
		}
		if(this.roadType<b.roadType){
			return -1;
		}
		if(this.roadType>b.roadType){
			return 1;
		}
		if(this.value<b.value){
			return -1;
		}
		return 1;
	}

}
