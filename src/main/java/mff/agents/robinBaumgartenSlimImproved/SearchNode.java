package mff.agents.robinBaumgartenSlimImproved;

import java.util.ArrayList;

import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import mff.forwardmodel.slim.core.MarioWorldSlim;

public class SearchNode {
    public int timeElapsed;
    public float remainingTimeEstimated = 0;
    public float remainingTime = 0;

    public SearchNode parentPos;
    public MarioForwardModelSlim sceneSnapshot = null;
    public int distanceFromOrigin = 0;
    public boolean hasBeenHurt = false;
    public boolean isInVisitedList = false;

    boolean[] action;
    int repetitions;

    public float cost;

    public float calcRemainingTime(float marioX, float marioXA) {
        return (100000 - (maxForwardMovement(marioXA, 1000) + marioX)) / Helper.maxMarioSpeed - 1000;
    }

    public float getRemainingTime() {
        if (remainingTime > 0)
            return remainingTime;
        else
            return remainingTimeEstimated;
    }

    public float estimateRemainingTimeChild(boolean[] action, int repetitions) {
        float[] childbehaviorDistanceAndSpeed = Helper.estimateMaximumForwardMovement(
                this.sceneSnapshot.getMarioFloatVelocity()[0], action, repetitions);
        return calcRemainingTime(this.sceneSnapshot.getMarioX() + childbehaviorDistanceAndSpeed[0],
                childbehaviorDistanceAndSpeed[1]);
    }

    public SearchNode(boolean[] action, int repetitions, SearchNode parent) {
        this.parentPos = parent;
        if (parent != null) {
            this.remainingTimeEstimated = parent.estimateRemainingTimeChild(action, repetitions);
            this.distanceFromOrigin = parent.distanceFromOrigin + 1;
        }
        this.action = action;
        this.repetitions = repetitions;
        if (parent != null)
            timeElapsed = parent.timeElapsed + repetitions;
        else
            timeElapsed = 0;
        calculateCost();
    }

    public void initializeRoot(MarioForwardModelSlim model) {
        if (this.parentPos == null) {
            this.sceneSnapshot = model.clone();
            this.remainingTimeEstimated = calcRemainingTime(model.getMarioX(), 0);
        }
    }

    public float simulatePos() {
        this.sceneSnapshot = parentPos.sceneSnapshot.clone();
        for (int i = 0; i < repetitions; i++) {
            this.sceneSnapshot.advance(action);
        }
        int marioDamage = Helper.getMarioDamage(this.sceneSnapshot, this.parentPos.sceneSnapshot);
        remainingTime =
                calcRemainingTime(this.sceneSnapshot.getMarioX(), this.sceneSnapshot.getMarioFloatVelocity()[0]) +
                        marioDamage * (1000000 - 100 * distanceFromOrigin);
        if (isInVisitedList)
            remainingTime += Helper.visitedListPenalty;
        hasBeenHurt = marioDamage != 0;

        return remainingTime;
    }

    public ArrayList<SearchNode> generateChildren() {
        ArrayList<SearchNode> list = new ArrayList<>();
        if (this.isLeafNode()) {
            return list;
        }
        ArrayList<boolean[]> possibleActions = Helper.getPossibleActions(this);
        for (boolean[] action : possibleActions) {
            list.add(new SearchNode(action, repetitions, this));
        }
        return list;
    }

    public boolean isLeafNode() {
        if (this.sceneSnapshot == null) {
            return false;
        }
        return this.sceneSnapshot.getGameStatusCode() != MarioWorldSlim.RUNNING;
    }

    private float maxForwardMovement(float initialSpeed, float ticks) {
        return (float) (99.17355373 * Math.pow(0.89, ticks + 1) - 9.090909091 * initialSpeed * Math.pow(0.89, ticks + 1) + 10.90909091 * ticks
                - 88.26446282 + 9.090909091 * initialSpeed);
    }

    public void calculateCost() {
        this.cost = getRemainingTime() + timeElapsed * 0.9f;
    }
}
