package micropolisj.engine;

public class SpecifiedTile {
	private CityLocation loc;
	private RoadSpecifiedTile pred;
	// -1 standard value for coordinates if there doesn't exist a predecessor
	private int costs=-1;
	private boolean type;
	// type=0: not ready --- type=1: ready
	private int bestcosts=-1;
	private int roadType;
	
	public SpecifiedTile(int cost, RoadSpecifiedTile pred, boolean type, int RoadType){
		this.type=type;
		assert type;
		this.pred=new RoadSpecifiedTile(new CityLocation(pred.getLocation().x,pred.getLocation().y),pred.getRoadType());
		this.costs=cost;
		this.roadType=RoadType;
			
	}
	public SpecifiedTile(CityLocation Loc, RoadSpecifiedTile pred1, boolean type, int RoadType){
		this.type=type;
		assert !type;
		this.pred=new RoadSpecifiedTile(new CityLocation(pred1.getLocation().x,pred1.getLocation().y),pred1.getRoadType());
		this.loc=new CityLocation(Loc.x,Loc.y);
		this.roadType=RoadType;
	}
	/**
	 * default constructor creates ready tile without pred
	 */
	public SpecifiedTile(int RoadType){
		this.type=true;
		this.pred= new RoadSpecifiedTile(new CityLocation(-1,-1),0);
		this.costs=0;
		this.roadType=RoadType;
	}
	
	public CityLocation getLoc(){
		return new CityLocation(loc.x,loc.y);
	}
	
	public int getRoadType(){
		return roadType;
	}
	
	public RoadSpecifiedTile getPred(){
		return pred;
	}
	
	public void setPred(RoadSpecifiedTile newpred){
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
