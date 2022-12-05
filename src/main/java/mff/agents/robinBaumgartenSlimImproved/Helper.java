package mff.agents.robinBaumgartenSlimImproved;

import java.util.ArrayList;

import engine.helper.MarioActions;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import mff.forwardmodel.slim.core.MarioWorldSlim;

public class Helper {
    public static final int visitedListPenalty = 1500;
    public static final float maxMarioSpeed = 10.9090909f;

    public static int getMarioDamage(MarioForwardModelSlim model, MarioForwardModelSlim prevModel) {
        int damage = 0;
        if (prevModel.getMarioMode() > model.getMarioMode()) {
            damage += 1;
        }
        if (model.getGameStatusCode() == MarioWorldSlim.LOSE) {
            if (model.getMarioY() > model.getLevelFloatDimensions()[1] - 20) {
                damage += 5;
            } else {
                damage += 2;
            }
        }
        return damage;
    }

    public static float[] estimateMaximumForwardMovement(float currentAccel, boolean[] action, int ticks) {
        float dist = 0;
        float runningSpeed = action[MarioActions.SPEED.getValue()] ? 1.2f : 0.6f;
        int dir = 0;
        if (action[MarioActions.LEFT.getValue()])
            dir = -1;
        if (action[MarioActions.RIGHT.getValue()])
            dir = 1;
        for (int i = 0; i < ticks; i++) {
            currentAccel += runningSpeed * dir;
            dist += currentAccel;
            currentAccel *= 0.89f;
        }
        float[] ret = new float[2];
        ret[0] = dist;
        ret[1] = currentAccel;
        return ret;
    }

    public static boolean canJumpHigher(SearchNode node, boolean checkParent) {
        if (node.parentPos != null && checkParent && canJumpHigher(node.parentPos, false))
            return true;
        return node.sceneSnapshot.mayMarioJump() || node.sceneSnapshot.getMarioCanJumpHigher();
    }

    public static ArrayList<boolean[]> getPossibleActions(SearchNode node) {
        if (canJumpHigher(node, true))
            return actionsWithJump;
        else
            return actionsNoJump;
    }

    private static final ArrayList<boolean[]> actionsNoJump = new ArrayList<>() {{
        // right, right speed, left, left speed
        add(new boolean[] { false, true, false, false, false });
        add(new boolean[] { false, true, false, false, true });
        add(new boolean[] { true, false, false, false, false });
        add(new boolean[] { true, false, false, false, true });
    }};

    private static final ArrayList<boolean[]> actionsWithJump = new ArrayList<>() {{
        // right, right speed, left, left speed
        add(new boolean[] { false, true, false, false, false });
        add(new boolean[] { false, true, false, false, true });
        add(new boolean[] { true, false, false, false, false });
        add(new boolean[] { true, false, false, false, true });
        // jump, jump right, jump right speed, jump left, jump left speed
        add(new boolean[] { false, false, false, true, false });
        add(new boolean[] { false, true, false, true, false });
        add(new boolean[] { false, true, false, true, true });
        add(new boolean[] { true, false, false, true, false });
        add(new boolean[] { true, false, false, true, true });
    }};
}
