package micropolisj.engine;

public class SpecifiedTile {
	private CityLocation loc;
	private CityLocation pred = new CityLocation(-1,-1);
	// -1 standard value for coordinates if there doesn't exist a predecessor
	private int costs=-1;
	private boolean type;
	// type=0: not ready --- type=1: ready
	private int bestcosts=-1;
	
	public SpecifiedTile(CityLocation loc, boolean type){
		this.type=type;
		this.loc=loc;
	}
	
	public CityLocation getLoc(){
		return loc;
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
	
	
}
