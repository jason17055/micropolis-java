// This file is part of DiverCity
// DiverCity is based on MicropolisJ
// Copyright (C) 2014 Arne Roland, Benjamin Kretz, Estela Gretenkord i Berenguer, Fabian Mett, Marvin Becker, Tom Brewe, Tony Schwedek, Ullika Scholz, Vanessa Schreck for DiverCity
//
// DiverCity is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.

package micropolisj.engine;

public class QuietExplosionSprite extends ExplosionSprite {
    QuietExplosionSprite(Micropolis engine, int x, int y) {
        super(engine, x, y);
    }

    public void moveImpl() {
        if (city.acycle % 2 == 0) {
            if (this.frame == 1) {
                //       city.makeSound(x/16, y/16, Sound.EXPLOSION_HIGH);
                city.sendMessageAt(MicropolisMessage.EXPLOSION_REPORT, x / 16, y / 16);
            }
            this.frame++;
        }

        if (this.frame > 6) {
            this.frame = 0;

            startFire(x / 16, y / 16);
            startFire(x / 16 - 1, y / 16 - 1);
            startFire(x / 16 + 1, y / 16 - 1);
            startFire(x / 16 - 1, y / 16 + 1);
            startFire(x / 16 + 1, y / 16 + 1);
            return;
        }
    }
}
