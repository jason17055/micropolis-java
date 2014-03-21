package micropolisj.engine;

public interface Technology {
    void apply();

    double getPointsNeeded();
    double getName();
    double getDescription();

    void Technology(String _name, String _description, double _pointsNeeded);
}
