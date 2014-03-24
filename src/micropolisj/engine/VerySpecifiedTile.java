package micropolisj.engine;

public class VerySpecifiedTile{
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

}
