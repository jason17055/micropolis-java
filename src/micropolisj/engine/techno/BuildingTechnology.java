package micropolisj.engine.techno;
import micropolisj.engine.*;
import micropolisj.engine.MicropolisTool;

public class BuildingTechnology extends GeneralTechnology {
    MicropolisTool tool_;

     public BuildingTechnology(double pointsNeeded_, String description_, String name_, MicropolisTool tool_){
        super(pointsNeeded_, description_, name_);
        this.tool_ = tool_;
    }


    public MicropolisTool getTool(){
        return tool_;
    }






}
