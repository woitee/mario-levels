package mff.agents.astarPlanningDynamic;

import mff.agents.astarHelper.CompareByCost;
import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarTree {
    public SearchNode winNode;

    public SearchNode furthestNode;
    public float furthestNodeDistance;

    float marioXStart;
    int searchSteps;
    boolean winFound = false;

    static float exitTileX;
    static final float maxMarioSpeedX = 10.91f;

    public int nodesEvaluated = 0;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCost());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();

    public void initPlanAhead(MarioForwardModelSlim startState, int searchSteps) {
        this.searchSteps = searchSteps;

        marioXStart = startState.getMarioX();

        furthestNode = getStartNode(startState);
        furthestNode.cost = calculateCostPlanToFinish(startState, furthestNode.nodeDepth);
        furthestNodeDistance = furthestNode.state.getMarioX();

        opened.add(furthestNode);
    }

    public void initPlanToFinish(MarioForwardModelSlim startState, int searchSteps) {
        this.searchSteps = searchSteps;

        marioXStart = startState.getMarioX();

        SearchNode startNode = getStartNode(startState);
        startNode.cost = calculateCostPlanToFinish(startState, startNode.nodeDepth);

        opened.add(startNode);
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

    private float calculateCostPlanAhead(MarioForwardModelSlim nextState, int nodeDepth) {
        float timeToFinish = (exitTileX - nextState.getMarioX()) / maxMarioSpeedX;
        timeToFinish *= 1.5f;
        return nodeDepth + timeToFinish;
    }

    private float calculateCostPlanToFinish(MarioForwardModelSlim nextState, int nodeDepth) {
        float timeToFinish = (exitTileX - nextState.getMarioX()) / maxMarioSpeedX;
        timeToFinish *= 1.1f;
        return nodeDepth + timeToFinish;
    }

    public void planAhead(MarioTimerSlim timer) {
        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
            SearchNode current = opened.remove();
            nodesEvaluated++;

            if (current.state.getGameStatusCode() == 1) {
                winNode = current;
                //System.out.print("WIN FOUND ");
                winFound = true;
                return;
            }

            if (current.state.getMarioX() > furthestNodeDistance) {
                furthestNode = current;
                furthestNodeDistance = current.state.getMarioX();

                if (furthestNode.nodeDepth * searchSteps > 40)
                    return;
            }

            ArrayList<MarioAction> actions = Helper.getPossibleActions(current.state);
            for (MarioAction action : actions) {
                MarioForwardModelSlim newState = current.state.clone();

                for (int i = 0; i < searchSteps; i++) {
                    newState.advance(action.value);
                }

                if (!newState.getWorld().mario.alive)
                    continue;

                float newStateCost = calculateCostPlanAhead(newState, current.nodeDepth + 1);

                int newStateCode = getIntState(newState);
                float newStateOldScore = visitedStates.getOrDefault(newStateCode, -1.0f);
                if (newStateOldScore >= 0 && newStateCost >= newStateOldScore)
                    continue;

                visitedStates.put(newStateCode, newStateCost);
                opened.add(getNewNode(newState, current, newStateCost, action));
            }
        }
    }

    public void planToFinish(MarioTimerSlim timer) {
        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
            SearchNode current = opened.remove();
            nodesEvaluated++;

            if (current.state.getGameStatusCode() == 1) {
                winNode = current;
                //System.out.print("WIN FOUND ");
                //System.out.println("Win depth: " + winNode.nodeDepth);
                winFound = true;
                return;
            }

            ArrayList<MarioAction> actions = Helper.getPossibleActions(current.state);
            for (MarioAction action : actions) {
                MarioForwardModelSlim newState = current.state.clone();

                for (int i = 0; i < searchSteps; i++) {
                    newState.advance(action.value);
                }

                if (!newState.getWorld().mario.alive)
                    continue;

                float newStateCost = calculateCostPlanToFinish(newState, current.nodeDepth + 1);

                int newStateCode = getIntState(newState);
                float newStateOldScore = visitedStates.getOrDefault(newStateCode, -1.0f);
                if (newStateOldScore >= 0 && newStateCost >= newStateOldScore)
                    continue;

                visitedStates.put(newStateCode, newStateCost);
                opened.add(getNewNode(newState, current, newStateCost, action));
            }
        }
//        System.out.println("ITERATIONS: " + iterations + " | OPENED SIZE: " + opened.size());
    }

    private boolean isSafe(SearchNode nodeToTest) {
        return nodeToTest.state.getWorld().mario.onGround;
    }

    public ArrayList<boolean[]> getTempSafePlan() {
        ArrayList<boolean[]> actionPlan = new ArrayList<>();

        SearchNode curr = furthestNode;

        while (curr.parent != null && !isSafe(curr)) {
            curr = curr.parent;
        }

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionPlan.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

        if (actionPlan.size() == 0) { // no safe path found
            //System.out.println("NO SAFE PATH FOUND");
            actionPlan.add(MarioAction.NO_ACTION.value);
        }

        return actionPlan;
    }

    public ArrayList<boolean[]> getPlanToFinish() {
        ArrayList<boolean[]> actionPlan = new ArrayList<>();

        SearchNode curr = winNode;

        //System.out.println("Win depth check: " + curr.nodeDepth);
        //System.out.println("Search steps: " + searchSteps);

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionPlan.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

        //System.out.println("Final plan size: " + actionPlan.size());

        return actionPlan;
    }
}
