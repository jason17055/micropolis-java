package micropolisj.engine.techno;

public interface Technology {
    boolean tryToApply();
    void addResearchPoints(double points);
    void resetResearchPoints();
    double getPointsNeeded();
    double getPointsUsed();
    String getName();
    String getDescription();
    boolean getIsResearched();

}
