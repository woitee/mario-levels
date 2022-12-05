package mff.forwardmodel.slim.sprites;

import engine.sprites.FlowerEnemy;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioUpdateContextSlim;

public class FlowerEnemySlim extends MarioSpriteSlim {
    private static final SpriteTypeCommon type = SpriteTypeCommon.ENEMY_FLOWER;
    private static final int width = 2;
    private static final int height = 12;

    private float yStart;
    private int waitTime;
    private float ya;

    private FlowerEnemySlim() { }

    public FlowerEnemySlim(FlowerEnemy originalFlowerEnemy) {
        this.x = originalFlowerEnemy.x;
        this.y = originalFlowerEnemy.y;
        this.alive = originalFlowerEnemy.alive;
        this.yStart = originalFlowerEnemy.getyStart();
        this.waitTime = originalFlowerEnemy.getWaitTime();
        this.ya = originalFlowerEnemy.ya;
    }

    // this constructor calls update on itself - needs context with world set
    public FlowerEnemySlim(float x, float y, MarioUpdateContextSlim updateContext) {
        this.x = x;
        this.y = y;
        this.alive = true;
        this.yStart = this.y;
        this.ya = -1;
        this.y -= 1;
        for (int i = 0; i < 4; i++) {
            this.update(updateContext);
        }
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowerEnemySlim that = (FlowerEnemySlim) o;
        boolean equal = Float.compare(that.yStart, yStart) == 0 &&
                waitTime == that.waitTime &&
                Float.compare(that.ya, ya) == 0  &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    FLOWER ENEMY EQUAL");
            return true;
        }
        else {
            System.out.println("    FLOWER ENEMY NOT EQUAL");
            return false;
        }
    }

    @Override
    public SpriteTypeCommon getType() {
        return type;
    }

    public MarioSpriteSlim clone() {
        FlowerEnemySlim clone = new FlowerEnemySlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.yStart = this.yStart;
        clone.waitTime = this.waitTime;
        clone.ya = this.ya;
        return clone;
    }

    @Override
    public void collideCheck(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -width * 2 - 4 && xMarioD < width * 2 + 4) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
               updateContext.world.mario.getHurt(updateContext);
            }
        }
    }

    @Override
    public void update(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        if (ya > 0) {
            if (y >= yStart) {
                y = yStart;
                int xd = (int) (Math.abs(updateContext.world.mario.x - x));
                waitTime++;
                if (waitTime > 40 && xd > 24) {
                    waitTime = 0;
                    ya = -1;
                }
            }
        } else if (ya < 0) {
            if (yStart - y > 20) {
                y = yStart - 20;
                waitTime++;
                if (waitTime > 40) {
                    waitTime = 0;
                    ya = 1;
                }
            }
        }
        y += ya;
    }

    @Override
    public boolean shellCollideCheck(ShellSlim shell, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return false;
        }

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < ShellSlim.height) {
                ya = -5;
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean fireballCollideCheck(FireballSlim fireball, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return false;
        }

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < FireballSlim.height) {
                ya = -5;
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public void bumpCheck(int xTile, int yTile, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16)) {
            ya = -5;
            updateContext.world.removeSprite(this, updateContext);
        }
    }
}
