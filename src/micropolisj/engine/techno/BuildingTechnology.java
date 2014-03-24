package micropolisj.engine.techno;
import micropolisj.engine.*;

public class BuildingTechnology implements Technology {
    double pointsNeeded;
    double pointsInvested;
    String name;
    String description;
    int buildingNumber;

    void BuildingTechnology(double pointsNeeded_, String description_, String name_, int buildingNumber_){
        pointsNeeded = pointsNeeded_;
        name = name_;
        description = description_;
        buildingNumber = buildingNumber_;
        pointsInvested = 0;
    }


    public void apply(){
        return;

    }

    public double getPointsNeeded(){
        return pointsNeeded;
    }


    public String getName(){
        return name;
    }


    public String getDescription(){
        return description;
    }


}
