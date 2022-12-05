package mff.agents.astarHelper;

public enum MarioAction {
    // LEFT, RIGHT, DOWN, SPEED, JUMP
    NO_ACTION(new boolean[] { false, false, false, false, false }),
    RIGHT_SPEED(new boolean[] { false, true, false, true, false }),
    RIGHT(new boolean[] { false, true, false, false, false }),
    LEFT(new boolean[] { true, false, false, false, false }),
    LEFT_SPEED(new boolean[] { true, false, false, true, false }),
    JUMP_RIGHT_SPEED(new boolean[] { false, true, false, true, true }),
    JUMP_RIGHT(new boolean[] { false, true, false, false, true }),
    JUMP(new boolean[] { false, false, false, false, true }),
    JUMP_LEFT(new boolean[] { true, false, false, false, true }),
    JUMP_LEFT_SPEED(new boolean[] { true, false, false, true, true });

    public boolean[] value;

    MarioAction(boolean[] value) {
        this.value = value;
    }
}
