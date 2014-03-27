package micropolisj.engine.techno;
import micropolisj.engine.*;

public class BuildingTechnology extends GeneralTechnology {
    MicropolisTool tool_;

     public BuildingTechnology(Micropolis engine_, double pointsNeeded_, String description_, String name_, MicropolisTool tool_, MicropolisMessage m){
        super(engine_, pointsNeeded_, description_, name_,m);
        this.tool_ = tool_;
    }
    @Override
    public boolean tryToApply(){
        if(super.tryToApply() == true){
            // reset building tech. can't be researched again
            this.isResearched = true;
            if(engine.selectedInfraTech.isSame(this)){
                engine.selectedInfraTech = null;
            } else {
               engine.selectedEETech = null;
            }
            return true;
        }
        return false;
    }


    public MicropolisTool getTool(){
        return tool_;
    }






}
