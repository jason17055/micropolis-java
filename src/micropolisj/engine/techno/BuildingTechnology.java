package micropolisj.engine.techno;
import micropolisj.engine.*;
import micropolisj.engine.MicropolisTool;

public class BuildingTechnology extends GeneralTechnology {
    MicropolisTool buildingNumber;

     public BuildingTechnology(double pointsNeeded_, String description_, String name_, MicropolisTool buildingNumber_){
        super(pointsNeeded_, description_, name_);
        buildingNumber = buildingNumber_;
    }


    public MicropolisTool getBuildingNumber(){
        return buildingNumber;
    }

}
