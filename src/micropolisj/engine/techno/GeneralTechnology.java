package micropolisj.engine.techno;
import micropolisj.engine.*;


public class GeneralTechnology implements Technology {
    double pointsNeeded;
    double pointsUsed;
    String name;
    String description;
    MicropolisTool buildingNumber;
    boolean isResearched;

    public GeneralTechnology(double pointsNeeded_, String description_, String name_){
        pointsNeeded = pointsNeeded_;
        name = name_;
        description = description_;

        pointsUsed = 0;
        isResearched = false;
    }


    public boolean tryToApply(){
        if(pointsUsed >= pointsNeeded){
            isResearched = true;
            resetResearchPoints();
            return true;
        }
        return false;
    }

    public double getPointsNeeded(){
        return pointsNeeded;
    }
    public void addResearchPoints(double points){
        pointsUsed += points;
        tryToApply();
    }
    public void resetResearchPoints(){
        pointsUsed = 0;
    }


    public String getName(){
        return name;
    }


    public String getDescription(){
        return description;
    }

    public MicropolisTool getBuildingNumber(){
        return buildingNumber;
    }

    public boolean getIsResearched(){
        return isResearched;
    }

    public double getPointsUsed(){
        return pointsUsed;
    }



}
