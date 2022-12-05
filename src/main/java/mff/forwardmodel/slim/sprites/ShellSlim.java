package mff.forwardmodel.slim.sprites;

import engine.sprites.Shell;
import mff.forwardmodel.common.SpriteTypeCommon;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioUpdateContextSlim;

public class ShellSlim extends MarioSpriteSlim {
    public static final float GROUND_INERTIA = 0.89f;
    public static final float AIR_INERTIA = 0.89f;
    private static final SpriteTypeCommon type = SpriteTypeCommon.SHELL;
    private static final int width = 4;
    static final int height = 12;

    private boolean onGround;

    private float xa, ya;
    int facing;

    private ShellSlim() { }

    public ShellSlim(Shell originalShell) {
        this.x = originalShell.x;
        this.y = originalShell.y;
        this.alive = originalShell.alive;
        this.xa = originalShell.xa;
        this.ya = originalShell.ya;
        this.facing = originalShell.facing;
        this.onGround = originalShell.isOnGround();
    }

    ShellSlim(float x, float y) {
        this.x = x;
        this.y = y;
        this.facing = 0;
        this.ya = -5;
        this.onGround = false;
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShellSlim that = (ShellSlim) o;
        boolean equal = onGround == that.onGround &&
                Float.compare(that.xa, xa) == 0 &&
                Float.compare(that.ya, ya) == 0 &&
                facing == that.facing &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    SHELL EQUAL");
            return true;
        }
        else {
            System.out.println("    SHELL NOT EQUAL");
            return false;
        }
    }

    @Override
    public SpriteTypeCommon getType() {
        return type;
    }

    public MarioSpriteSlim clone() {
        ShellSlim clone = new ShellSlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.onGround = this.onGround;
        clone.xa = this.xa;
        clone.ya = this.ya;
        clone.facing = this.facing;
        return clone;
    }

    @Override
    public void update(MarioUpdateContextSlim updateContext) {
        if (!this.alive) return;

        float sideWaysSpeed = 11f;

        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        if (facing != 0) {
            updateContext.shellsToCheck.add(this);
        }

        if (!move(xa, 0, updateContext)) {
            facing = -facing;
        }
        onGround = false;
        move(0, ya, updateContext);

        ya *= 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 2;
        }
    }

    @Override
    public boolean fireballCollideCheck(FireballSlim fireball, MarioUpdateContextSlim updateContext) {
        if (!this.alive) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < FireballSlim.height) {
                if (facing != 0)
                    return true;

                xa = fireball.facing * 2;
                ya = -5;
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public void collideCheck(MarioUpdateContextSlim updateContext) {
        if (!this.alive) return;

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
                if (updateContext.world.mario.ya > 0 && yMarioD <= 0 && (!updateContext.world.mario.onGround || !updateContext.world.mario.wasOnGround)) {
                    updateContext.world.mario.stomp(this, updateContext);
                    if (facing != 0) {
                        xa = 0;
                        facing = 0;
                    } else {
                        facing = updateContext.world.mario.facing;
                    }
                } else {
                    if (facing != 0) {
                        updateContext.world.mario.getHurt(updateContext);
                    } else {
                        updateContext.world.mario.kick();
                        facing = updateContext.world.mario.facing;
                    }
                }
            }
        }
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
            if (isBlocking(x + xa + width, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, ya, updateContext))
                collide = true;
        }
        else if (xa < 0) {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya, xa, ya, updateContext))
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
            if (isBlocking(x + xa - width, y + ya, xa, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya, updateContext))
                collide = true;
        }
        else if (ya < 0) {
            if (isBlocking(x + xa, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya - height, xa, ya, updateContext))
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

    private boolean isBlocking(float _x, float _y, float xa, float ya, MarioUpdateContextSlim updateContext) {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16))
            return false;

        boolean blocking = updateContext.world.level.isBlocking(x, y, ya);

        if (blocking && ya == 0 && xa != 0) {
            updateContext.world.bump(x, y, true, updateContext);
        }

        return blocking;
    }

    @Override
    public void bumpCheck(int xTile, int yTile, MarioUpdateContextSlim updateContext) {
        if (!this.alive) return;

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16)) {
            facing = -updateContext.world.mario.facing;
            ya = -10;
        }
    }

    @Override
    public boolean shellCollideCheck(ShellSlim shell, MarioUpdateContextSlim updateContext) {
        if (!this.alive) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < height) {
                if (this != shell) {
                    updateContext.world.removeSprite(shell, updateContext);
                }
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }
}
