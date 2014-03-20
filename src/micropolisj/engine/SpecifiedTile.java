package micropolisj.engine;

public class SpecifiedTile {
	private CityLocation loc;
	private CityLocation pred = new CityLocation(-1,-1);
	// -1 standard value for coordinates if there doesn't exist a predecessor
	private int costs=-1;
	private boolean type;
	// type=0: not ready --- type=1: ready
	private int bestcosts=-1;
	private int roadType;
	
	public SpecifiedTile(int cost, CityLocation loc, boolean type, int RoadType){
		this.type=type;
		assert type;
		this.pred=new CityLocation(loc.x,loc.y);
		this.costs=cost;
		this.roadType=RoadType;
			
	}
	public SpecifiedTile(CityLocation loc, CityLocation pred, boolean type, int RoadType){
		this.type=type;
		assert !type;
		this.pred=new CityLocation(pred.x,pred.y);
		this.loc=new CityLocation(loc.x,loc.y);
		this.roadType=RoadType;
	}
	/**
	 * default constructor creates ready tile without pred
	 */
	public SpecifiedTile(int RoadType){
		this.type=true;
		this.pred=new CityLocation(-1,-1);
		this.costs=0;
		this.roadType=RoadType;
	}
	
	public CityLocation getLoc(){
		return new CityLocation(loc.x,loc.y);
	}
	
	public int getRoadType(){
		return roadType;
	}
	
	public CityLocation getPred(){
		return pred;
	}
	
	public void setPred(CityLocation newpred){
		pred=newpred;
	}
	
	public int getCosts(){
		return costs;
	}
	
	public void setCosts(int newcosts){
		costs=newcosts;
	}
	public void setType(boolean newtype){
		type=newtype;
	}
	
	public void setBestCosts(int i){
		bestcosts=i;
	}
	
	public int getBestCosts(){
		return bestcosts;
	}
	public boolean getType() {
		return type;
	}
	public static boolean equals(SpecifiedTile a, SpecifiedTile b){
		if(a.type&&b.type){
			if(a.costs==b.costs&&a.pred==b.pred&&a.roadType==b.roadType){
				return true;
			}else{
				return false;
			}
		}else{
			if(!a.type&&!b.type){
				if(a.loc==b.loc&&a.pred==b.pred&&a.roadType==b.roadType){
					return true;
				}else{
					return false;
				}
			}
		}
		return false;
	}
}
