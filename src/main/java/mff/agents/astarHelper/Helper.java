package mff.agents.astarHelper;

import java.util.ArrayList;

import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class Helper {
    public static boolean canJumpHigher(MarioForwardModelSlim state) {
        return state.mayMarioJump() || state.getMarioCanJumpHigher();
    }

    public static ArrayList<MarioAction> getPossibleActions(MarioForwardModelSlim state) {
        if (canJumpHigher(state))
            return actionsWithJump;
        else
            return actionsNoJump;
    }

    private static final ArrayList<MarioAction> actionsNoJump = new ArrayList<>() {{
        add(MarioAction.RIGHT_SPEED);
        //add(MarioAction.RIGHT);
        //add(MarioAction.LEFT);
        add(MarioAction.LEFT_SPEED);
    }};

    private static final ArrayList<MarioAction> actionsWithJump = new ArrayList<>() {{
        add(MarioAction.RIGHT_SPEED);
        //add(MarioAction.RIGHT);
        //add(MarioAction.LEFT);
        add(MarioAction.LEFT_SPEED);

        add(MarioAction.JUMP_RIGHT_SPEED);
        //add(MarioAction.JUMP_RIGHT);
        //add(MarioAction.JUMP);
        //add(MarioAction.JUMP_LEFT);
        add(MarioAction.JUMP_LEFT_SPEED);
    }};
}
