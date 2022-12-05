package mff.forwardmodel.slim.sprites;

import engine.sprites.Fireball;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioUpdateContextSlim;
import mff.forwardmodel.common.SpriteTypeCommon;

public class FireballSlim extends MarioSpriteSlim {
    public static final float GROUND_INERTIA = 0.89f;
    public static final float AIR_INERTIA = 0.89f;
    private static final SpriteTypeCommon type = SpriteTypeCommon.FIREBALL;
    private static final int width = 4;
    static final int height = 8;

    private float xa, ya;
    int facing;
    private boolean onGround;

    private FireballSlim() { }

    public FireballSlim(Fireball originalFireball) {
        this.x = originalFireball.x;
        this.y = originalFireball.y;
        this.alive = originalFireball.alive;
        this.xa = originalFireball.xa;
        this.ya = originalFireball.ya;
        this.facing = originalFireball.facing;
        this.onGround = originalFireball.isOnGround();
    }

    FireballSlim(float x, float y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.ya = 4;
        this.onGround = false;
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FireballSlim that = (FireballSlim) o;
        boolean equal = Float.compare(that.xa, xa) == 0 &&
                Float.compare(that.ya, ya) == 0 &&
                facing == that.facing &&
                onGround == that.onGround  &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    FIREBALL EQUAL");
            return true;
        }
        else {
            System.out.println("    FIREBALL NOT EQUAL");
            return false;
        }
    }

    @Override
    public SpriteTypeCommon getType() {
        return type;
    }

    public MarioSpriteSlim clone() {
        FireballSlim clone = new FireballSlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.xa = this.xa;
        clone.ya = this.ya;
        clone.facing = this.facing;
        clone.onGround = this.onGround;
        return clone;
    }

    // either xa or ya is always zero
    private boolean move(float xa, float ya, MarioUpdateContextSlim updateContext) {
        if (xa != 0) {
            float stepX = Math.signum(xa) * 8;
            while (Math.abs(xa) > Math.abs(stepX)) {
                xa -= stepX;
                if (!moveStepX(stepX, updateContext))
                    return false;
            }
            return moveStepX(xa, updateContext);
        } else {
            float stepY = Math.signum(ya) * 8;
            while (Math.abs(ya) > Math.abs(stepY)) {
                ya -= stepY;
                if (!moveStepY(stepY, updateContext))
                    return false;
            }
            return moveStepY(ya, updateContext);
        }
    }

    // return true if move is successful, false if blocked
    private boolean moveStepX(float xa, MarioUpdateContextSlim updateContext) {
        float ya = 0;
        boolean collide = false;
        if (xa > 0) {
            if (isBlocking(x + xa + width, y + ya - height, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya - height / 2, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, ya, updateContext))
                collide = true;
        }
        else if (xa < 0) {
            if (isBlocking(x + xa - width, y + ya - height, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya - height / 2, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya, ya, updateContext))
                collide = true;
        }
        if (collide) {
            if (xa < 0) {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            else if (xa > 0) {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            return false;
        } else {
            x += xa;
            return true;
        }
    }

    // return true if move is successful, false if blocked
    private boolean moveStepY(float ya, MarioUpdateContextSlim updateContext) {
        float xa = 0;
        boolean collide = false;
        if (ya > 0) {
            if (isBlocking(x + xa - width, y + ya, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, ya, updateContext))
                collide = true;
        }
        else if (ya < 0) {
            if (isBlocking(x + xa, y + ya - height, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya - height, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya - height, ya, updateContext))
                collide = true;
        }

        if (collide) {
            if (ya < 0) {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            else if (ya > 0) {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        } else {
            y += ya;
            return true;
        }
    }

    private boolean isBlocking(float _x, float _y, float ya, MarioUpdateContextSlim updateContext) {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16))
            return false;

        return updateContext.world.level.isBlocking(x, y, ya);
    }

    public void update(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        float sideWaysSpeed = 8f;
        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }
        xa = facing * sideWaysSpeed;

        updateContext.fireballsToCheck.add(this);

        if (!move(xa, 0, updateContext)) {
            updateContext.world.removeSprite(this, updateContext);
            return;
        }

        onGround = false;
        move(0, ya, updateContext);
        if (onGround)
            ya = -10;

        ya *= 0.95f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 1.5;
        }
    }
}
