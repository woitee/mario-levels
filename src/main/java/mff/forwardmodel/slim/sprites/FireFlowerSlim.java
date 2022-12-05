package mff.forwardmodel.slim.sprites;

import engine.sprites.FireFlower;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioUpdateContextSlim;

public class FireFlowerSlim extends MarioSpriteSlim {
    private static final int height = 12;
    private static final SpriteTypeCommon type = SpriteTypeCommon.FIRE_FLOWER;

    private int life;

    private FireFlowerSlim() { }

    public FireFlowerSlim(FireFlower originalFireFlower) {
        this.x = originalFireFlower.x;
        this.y = originalFireFlower.y;
        this.alive = originalFireFlower.alive;
        this.life = originalFireFlower.getLife();
    }

    public FireFlowerSlim(float x, float y) {
        this.x = x;
        this.y = y;
        this.life = 0;
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FireFlowerSlim that = (FireFlowerSlim) o;
        boolean equal = life == that.life  &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    FIRE FLOWER EQUAL");
            return true;
        }
        else {
            System.out.println("    FIRE FLOWER NOT EQUAL");
            return false;
        }
    }

    @Override
    public SpriteTypeCommon getType() {
        return type;
    }

    public MarioSpriteSlim clone() {
        FireFlowerSlim clone = new FireFlowerSlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.life = this.life;
        return clone;
    }

    @Override
    public void collideCheck(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
                updateContext.world.mario.getFlower(updateContext);
                updateContext.world.removeSprite(this, updateContext);
            }
        }
    }

    @Override
    public void update(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

       life++;
        if (life < 9) {
            this.y--;
        }
    }
}
