package micropolisj.engine;

/**
 * Created with IntelliJ IDEA.
 * User: ullika
 * Date: 3/20/14
 * Time: 8:05 PM
 */
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
