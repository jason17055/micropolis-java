// This file is part of DiverCity
// DiverCity is based on MicropolisJ
// Copyright (C) 2014 Arne Roland, Benjamin Kretz, Estela Gretenkord i Berenguer, Fabian Mett, Marvin Becker, Tom Brewe, Tony Schwedek, Ullika Scholz, Vanessa Schreck for DiverCity
//
// DiverCity is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

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
