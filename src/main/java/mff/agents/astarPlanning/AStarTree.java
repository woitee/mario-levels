package mff.agents.astarPlanning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import mff.agents.astarHelper.CompareByCost;
import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.*;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class AStarTree {
    public SearchNode bestNode;
    public float bestNodeCost;

    float marioXStart;
    int searchSteps;
    boolean finished = false;

    int iterations = 0;

    static float exitTileX;
    static final float maxMarioSpeedX = 10.91f;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCost());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();

    public AStarTree(MarioForwardModelSlim startState, int searchSteps) {
        this.searchSteps = searchSteps;

        marioXStart = startState.getMarioX();

        bestNode = getStartNode(startState);
        bestNode.cost = calculateCost(startState, bestNode.nodeDepth);

        opened.add(bestNode);
    }

    private int getIntState(MarioForwardModelSlim model) {
        return getIntState((int) model.getMarioX(), (int) model.getMarioY());
    }

    private int getIntState(int x, int y) {
        return (x << 16) | y;
    }

    private SearchNode getStartNode(MarioForwardModelSlim state) {
        return new SearchNode(state);
    }

    private SearchNode getNewNode(MarioForwardModelSlim state, SearchNode parent, float cost, MarioAction action) {
        return new SearchNode(state, parent, cost, action);
    }

    private float calculateCost(MarioForwardModelSlim nextState, int nodeDepth) {
        float timeToFinish = (exitTileX - nextState.getMarioX()) / maxMarioSpeedX;
        timeToFinish *= 1.1f;
        return nodeDepth + timeToFinish;
    }

    public void search(MarioTimerSlim timer) {
        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
            iterations++;
            SearchNode current = opened.remove();

            if (current.cost < bestNodeCost) {
                bestNode = current;
                bestNodeCost = current.cost;
            }

            if (current.state.getGameStatusCode() == 1) {
                bestNode = current;
                //System.out.print("WIN FOUND ");
                finished = true;
                break;
            }

            ArrayList<MarioAction> actions = Helper.getPossibleActions(current.state);
            for (MarioAction action : actions) {
                MarioForwardModelSlim newState = current.state.clone();

                for (int i = 0; i < searchSteps; i++) {
                    newState.advance(action.value);
                }

                if (!newState.getWorld().mario.alive)
                    continue;

                float newStateCost = calculateCost(newState, current.nodeDepth + 1);

                int newStateCode = getIntState(newState);
                float newStateOldScore = visitedStates.getOrDefault(newStateCode, -1.0f);
                if (newStateOldScore >= 0 && newStateCost >= newStateOldScore)
                    continue;

                visitedStates.put(newStateCode, newStateCost);
                opened.add(getNewNode(newState, current, newStateCost, action));
            }
        }
//        System.out.println("ITERATIONS: " + iterations + " | Best X: " + bestNode.state.getMarioX());
//        System.out.println(opened.size());
    }

    public ArrayList<boolean[]> getActionPlan() {
        ArrayList<boolean[]> actionPlan = new ArrayList<>();

        SearchNode curr = bestNode;

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionPlan.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

        return actionPlan;
    }
}
