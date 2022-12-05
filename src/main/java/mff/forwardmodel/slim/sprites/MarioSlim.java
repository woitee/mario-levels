package mff.forwardmodel.slim.sprites;

import engine.helper.MarioActions;
import engine.sprites.Mario;
import mff.forwardmodel.common.LevelPart;
import mff.forwardmodel.slim.core.MarioSpriteSlim;
import mff.forwardmodel.slim.core.MarioUpdateContextSlim;
import mff.forwardmodel.common.SpriteTypeCommon;

public class MarioSlim extends MarioSpriteSlim {
    private static final SpriteTypeCommon type = SpriteTypeCommon.MARIO;
    private static final int width = 4;
    public static final float GROUND_INERTIA = 0.89f;
    public static final float AIR_INERTIA = 0.89f;
    private static final int POWERUP_TIME = 3;

    public int height;
    public int invulnerableTime;
    public boolean onGround, wasOnGround;
    public boolean isDucking, mayJump, canShoot, isLarge, isFire, oldLarge, oldFire;
    public float xa;
    public float ya;
    public int facing;
    public int jumpTime;
    public float xJumpSpeed, yJumpSpeed, xJumpStart;

    private MarioSlim() { }

    public MarioSlim(Mario originalMario) {
        Mario.PrivateMarioCopyInfo info = originalMario.getPrivateMarioCopyInfo();
        this.invulnerableTime = info.invulnerableTime;
        this.oldLarge = info.oldLarge;
        this.oldFire = info.oldFire;
        this.xJumpSpeed = info.xJumpSpeed;
        this.yJumpSpeed = info.yJumpSpeed;
        this.xJumpStart = info.xJumpStart;

        this.x = originalMario.x;
        this.y = originalMario.y;
        this.alive = originalMario.alive;
        this.height = originalMario.height;
        this.wasOnGround = originalMario.wasOnGround;
        this.onGround = originalMario.onGround;
        this.isDucking = originalMario.isDucking;
        this.isLarge = originalMario.isLarge;
        this.mayJump = originalMario.mayJump;
        this.canShoot = originalMario.canShoot;
        this.isFire = originalMario.isFire;
        this.xa = originalMario.xa;
        this.ya = originalMario.ya;
        this.facing = originalMario.facing;
        this.jumpTime = originalMario.jumpTime;
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarioSlim that = (MarioSlim) o;
        boolean equal = height == that.height &&
                invulnerableTime == that.invulnerableTime &&
                onGround == that.onGround &&
                wasOnGround == that.wasOnGround &&
                isLarge == that.isLarge &&
                isDucking == that.isDucking &&
                mayJump == that.mayJump &&
                canShoot == that.canShoot &&
                isFire == that.isFire &&
                oldLarge == that.oldLarge &&
                oldFire == that.oldFire &&
                Float.compare(that.xa, xa) == 0 &&
                Float.compare(that.ya, ya) == 0 &&
                facing == that.facing &&
                jumpTime == that.jumpTime &&
                Float.compare(that.xJumpSpeed, xJumpSpeed) == 0 &&
                Float.compare(that.yJumpSpeed, yJumpSpeed) == 0 &&
                Float.compare(that.xJumpStart, xJumpStart) == 0 &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    MARIO EQUAL");
            return true;
        }
        else {
            System.out.println("    MARIO NOT EQUAL");
            return false;
        }
    }

    @Override
    public SpriteTypeCommon getType() {
        return type;
    }

    public MarioSpriteSlim clone() {
        MarioSlim clone = new MarioSlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.height = this.height;
        clone.invulnerableTime = this.invulnerableTime;
        clone.onGround = this.onGround;
        clone.wasOnGround = this.wasOnGround;
        clone.isLarge = this.isLarge;
        clone.isDucking = this.isDucking;
        clone.mayJump = this.mayJump;
        clone.canShoot = this.canShoot;
        clone.isFire = this.isFire;
        clone.oldLarge = this.oldLarge;
        clone.oldFire = this.oldFire;
        clone.xa = this.xa;
        clone.ya = this.ya;
        clone.facing = this.facing;
        clone.jumpTime = this.jumpTime;
        clone.xJumpSpeed = this.xJumpSpeed;
        clone.yJumpSpeed = this.yJumpSpeed;
        clone.xJumpStart = this.xJumpStart;
        return clone;
    }

    @Override
    public void update(MarioUpdateContextSlim updateContext) {
        if (!alive) return;

        if (invulnerableTime > 0) {
            invulnerableTime--;
        }
        this.wasOnGround = this.onGround;

        float sideWaysSpeed = updateContext.actions[MarioActions.SPEED.getValue()] ? 1.2f : 0.6f;

        if (onGround) {
            isDucking = updateContext.actions[MarioActions.DOWN.getValue()] && isLarge;
        }

        if (isLarge) {
            height = isDucking ? 12 : 24;
        } else {
            height = 12;
        }

        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        if (updateContext.actions[MarioActions.JUMP.getValue()] || (jumpTime < 0 && !onGround)) {
            if (jumpTime < 0) {
                xa = xJumpSpeed;
                ya = -jumpTime * yJumpSpeed;
                jumpTime++;
            } else if (onGround && mayJump) {
                xJumpSpeed = 0;
                yJumpSpeed = -1.9f;
                jumpTime = 7;
                ya = jumpTime * yJumpSpeed;
                onGround = false;
                if (!(isBlocking(x, y - 4 - height, -4, updateContext)
                        || isBlocking(x - width, y - 4 - height, -4, updateContext)
                        || isBlocking(x + width, y - 4 - height, -4, updateContext))) {
                    this.xJumpStart = this.x;
                }
            } else if (jumpTime > 0) {
                xa += xJumpSpeed;
                ya = jumpTime * yJumpSpeed;
                jumpTime--;
            }
        } else {
            jumpTime = 0;
        }

        if (updateContext.actions[MarioActions.LEFT.getValue()] && !isDucking) {
            xa -= sideWaysSpeed;
            if (jumpTime >= 0)
                facing = -1;
        }

        if (updateContext.actions[MarioActions.RIGHT.getValue()] && !isDucking) {
            xa += sideWaysSpeed;
            if (jumpTime >= 0)
                facing = 1;
        }

        if (updateContext.actions[MarioActions.SPEED.getValue()] && canShoot && isFire && updateContext.fireballsOnScreen < 2) {
            updateContext.world.addSprite(new FireballSlim(x + facing * 6, y - 20, facing), updateContext);
        }

        canShoot = !updateContext.actions[MarioActions.SPEED.getValue()];

        mayJump = onGround && !updateContext.actions[MarioActions.JUMP.getValue()];

        if (Math.abs(xa) < 0.5f) {
            xa = 0;
        }

        onGround = false;
        move(xa, 0, updateContext);
        move(0, ya, updateContext);
        if (!wasOnGround && onGround && this.xJumpStart >= 0) {
            this.xJumpStart = -100;
        }

        if (x < 0) {
            x = 0;
            xa = 0;
        }

        if (x > updateContext.world.level.exitTileX * 16) {
            x = updateContext.world.level.exitTileX * 16;
            xa = 0;
            updateContext.world.win();
        }

        ya *= 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            ya += 3;
        }
    }

    private boolean isBlocking(float _x, float _y, float ya, MarioUpdateContextSlim updateContext) {
        int xTile = (int) (_x / 16);
        int yTile = (int) (_y / 16);
        if (xTile == (int) (this.x / 16) && yTile == (int) (this.y / 16))
            return false;

        boolean blocking = updateContext.world.level.isBlocking(xTile, yTile, ya);
        byte blockValue = updateContext.world.level.getBlockValue(xTile, yTile);

        if (blockValue == LevelPart.COIN.getValue()) {
            this.collectCoin(updateContext);
            updateContext.world.level.setBlock(xTile, yTile, 0);
        }
        if (blocking && ya < 0) {
            updateContext.world.bump(xTile, yTile, isLarge, updateContext);
        }
        return blocking;
    }

    // either xa or ya is always zero
    private void move(float xa, float ya, MarioUpdateContextSlim updateContext) {
        if (xa != 0) {
            float stepX = Math.signum(xa) * 8;
            while (Math.abs(xa) > Math.abs(stepX)) {
                xa -= stepX;
                if (!moveStepX(stepX, updateContext))
                    return;
            }
            moveStepX(xa, updateContext);
        } else {
            float stepY = Math.signum(ya) * 8;
            while (Math.abs(ya) > Math.abs(stepY)) {
                ya -= stepY;
                if (!moveStepY(stepY, updateContext))
                    return;
            }
            moveStepY(ya, updateContext);
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
                jumpTime = 0;
                this.ya = 0;
            }
            else if (ya > 0) {
                y = (int) ((y - 1) / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        } else {
            y += ya;
            return true;
        }
    }

    public void stomp(EnemySlim enemy, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }
        float targetY = enemy.y - enemy.height / 2;
        move(0, targetY - y, updateContext);

        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    public void stomp(ShellSlim shell, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }
        float targetY = shell.y - ShellSlim.height / 2;
        move(0, targetY - y, updateContext);

        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    public void getHurt(MarioUpdateContextSlim updateContext) {
        if (invulnerableTime > 0 || !this.alive)
            return;

        if (isLarge) {
            updateContext.world.pauseTimer = 3 * POWERUP_TIME;
            this.oldLarge = true;
            this.oldFire = this.isFire;
            if (isFire) {
                this.isFire = false;
            } else {
                this.isLarge = false;
            }
            invulnerableTime = 32;
        } else {
            if (updateContext.world != null) {
                updateContext.world.lose();
            }
        }
    }

    void getFlower(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        if (!isFire) {
            updateContext.world.pauseTimer = 3 * POWERUP_TIME;
            this.oldFire = false;
            this.oldLarge = this.isLarge;
            this.isFire = true;
            this.isLarge = true;
        } else {
            this.collectCoin(updateContext);
        }
    }

    void getMushroom(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        if (!isLarge) {
            updateContext.world.pauseTimer = 3 * POWERUP_TIME;
            this.oldFire = this.isFire;
            this.oldLarge = false;
            this.isLarge = true;
        } else {
            this.collectCoin(updateContext);
        }
    }

    void kick() {
        if (!this.alive) {
            return;
        }

        invulnerableTime = 1;
    }

    public void stomp(BulletBillSlim bill, MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        float targetY = bill.y - BulletBillSlim.height / 2;
        move(0, targetY - y, updateContext);

        xJumpSpeed = 0;
        yJumpSpeed = -1.9f;
        jumpTime = 8;
        ya = jumpTime * yJumpSpeed;
        onGround = false;
        invulnerableTime = 1;
    }

    void collect1Up(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        updateContext.world.lives++;
    }

    public void collectCoin(MarioUpdateContextSlim updateContext) {
        if (!this.alive) {
            return;
        }

        updateContext.world.coins++;
        if (updateContext.world.coins % 100 == 0) {
            collect1Up(updateContext);
        }
    }
}
