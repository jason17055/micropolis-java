package micropolisj.engine.techno;

public class StreetUpgradeTech extends GeneralTechnology {
    public StreetUpgradeTech(double pointsNeeded_, String description_, String name_){
        super(pointsNeeded_, description_, name_);
    }

    public boolean tryToApply(){
        if(super.tryToApply() == true){
            // do some fancy street Upgrade stuff
            System.out.println("street upgrade");
            return true;
        }
        return false;
    }
}
