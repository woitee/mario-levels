package mff.forwardmodel.slim.core;

public class MarioForwardModelSlim {

    private final MarioWorldSlim world;

    public MarioForwardModelSlim(MarioWorldSlim world) {
        this.world = world;
    }

    public void advance(boolean[] actions) {
        this.world.update(actions);
    }

    public void advanceWindow(boolean[] actions, int rightWindowBorderX) {
        this.world.updateWindow(actions, rightWindowBorderX);
    }

    public boolean deepEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarioForwardModelSlim that = (MarioForwardModelSlim) o;
        return world.deepEquals(that.world);
    }

    public MarioForwardModelSlim clone() {
        return new MarioForwardModelSlim(this.world.clone());
    }

    public MarioWorldSlim getWorld() {
        return this.world.clone();
    }

    public int getGameStatusCode() {
        return this.world.gameStatusCode;
    }

    public float[] getMarioFloatPos() {
        return new float[] { this.world.mario.x, this.world.mario.y };
    }

    public float getMarioX() {
        return this.world.mario.x;
    }

    public float getMarioY() {
        return this.world.mario.y;
    }

    public float[] getMarioFloatVelocity() {
        return new float[] { this.world.mario.xa, this.world.mario.ya };
    }

    public float[] getLevelFloatDimensions() {
        return new float[] { this.world.level.width, this.world.level.height };
    }

    public int getMarioMode() {
        int value = 0;
        if (this.world.mario.isLarge) {
            value = 1;
        }
        if (this.world.mario.isFire) {
            value = 2;
        }
        return value;
    }

    public boolean getMarioCanJumpHigher() {
        return this.world.mario.jumpTime > 0;
    }

    public boolean mayMarioJump() {
        return this.world.mario.mayJump;
    }
}
