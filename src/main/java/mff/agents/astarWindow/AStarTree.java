package mff.agents.astarWindow;

import mff.agents.astarHelper.CompareByCost;
import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import mff.forwardmodel.slim.core.MarioWorldSlim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarTree {
    public SearchNode furthestNode;
    public float furthestNodeDistance;

    float marioXStart;
    int rightWindowBorderX;
    //int furthestNonEmptyRightX;
    int searchSteps;

    static boolean winFound = false;
    static final float maxMarioSpeedX = 10.91f;
    static float exitTileX;

    public int nodesEvaluated = 0;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCost());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();
    
    public AStarTree(MarioForwardModelSlim startState, int searchSteps) {
    	this.searchSteps = searchSteps;

    	marioXStart = startState.getMarioX();
    	rightWindowBorderX = (int) (marioXStart + 176);
    	//furthestNonEmptyRightX = findNonEmptyColumn(rightWindowBorderX, startState) * 16; // plan to this

        //  dont plan all the way to the border - to allow seeing new enemies soon enough
        //furthestNonEmptyRightX = Math.min(furthestNonEmptyRightX, rightWindowBorderX - (MarioWorldSlim.marioGameWidth / 8));

    	furthestNode = getStartNode(startState);
    	furthestNode.cost = calculateCost(startState, furthestNode.nodeDepth);
    	furthestNodeDistance = furthestNode.state.getMarioX();
    	
    	opened.add(furthestNode);
    }

    private int findNonEmptyColumn(int rightBorderX, MarioForwardModelSlim model) {
        var level = model.getWorld().level;
        int rightBorderColumn = rightBorderX / 16;
        for (int x = rightBorderColumn; x >= rightBorderColumn - (MarioWorldSlim.marioGameWidth / 16 - 1); x--) {
            for (int y = 0; y < MarioWorldSlim.marioGameHeight / 16; y++) {
                if (isBlockSafe(level.getBlockValue(x, y)))
                    return x;
            }
        }
        throw new IllegalStateException("Level empty or check failed.");
    }

    private int getIntState(MarioForwardModelSlim model) {
    	return getIntState((int) model.getMarioX(), (int) model.getMarioY());
    }
    
    private int getIntState(int x, int y) {
    	return (x << 16) | y;
    }
    
    private SearchNode getStartNode(MarioForwardModelSlim state) {
    	// TODO: pooling
    	return new SearchNode(state);
    }
    
    private SearchNode getNewNode(MarioForwardModelSlim state, SearchNode parent, float cost, MarioAction action) {
    	// TODO: pooling
    	return new SearchNode(state, parent, cost, action);
    }
    
    private float calculateCost(MarioForwardModelSlim nextState, int nodeDepth) {
        float timeToFinish = (exitTileX - nextState.getMarioX()) / maxMarioSpeedX;
        timeToFinish *= 1.1;
        return nodeDepth + timeToFinish;
	}
    
    public ArrayList<boolean[]> search(MarioTimerSlim timer) {
        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
            SearchNode current = opened.remove();
            nodesEvaluated++;

            if (current.state.getMarioX() > furthestNodeDistance) {
                furthestNode = current;
                furthestNodeDistance = current.state.getMarioX();
            }

            if (current.state.getMarioX() >= rightWindowBorderX && isSafe(current)) { // right window border reached and position is safe
                furthestNode = current;
                //System.out.println("Right border found, X: " + furthestNode.state.getMarioX() + ", Y: " + furthestNode.state.getMarioY());
                if (current.state.getGameStatusCode() == 1) { // finish reached
                    winFound = true;
                    //System.out.println("WIN FOUND");
                }
                break;
            }

            if (current.state.getGameStatusCode() == 1) {
                furthestNode = current;
                //System.out.print("WIN FOUND");
                winFound = true;
                break;
            }

            ArrayList<MarioAction> actions = Helper.getPossibleActions(current.state);
            for (MarioAction action : actions) {
                MarioForwardModelSlim newState = current.state.clone();

                for (int i = 0; i < searchSteps; i++) {
                    newState.advanceWindow(action.value, rightWindowBorderX);
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

        ArrayList<boolean[]> actionsList = new ArrayList<>();

        SearchNode curr = furthestNode;

        if (winFound) {
            while (curr.parent != null) {
                for (int i = 0; i < searchSteps; i++)
                    actionsList.add(curr.marioAction.value);
                curr = curr.parent;
            }
        } else {
            while (curr.parent != null && !isSafe(curr)) {
                curr = curr.parent;
            }

            while (curr.parent != null) {
                for (int i = 0; i < searchSteps; i++) {
                    actionsList.add(curr.marioAction.value);
                }
                curr = curr.parent;
            }

            /*
            if (actionsList.size() == 0) { // no safe path found
                System.out.println("NO SAFE PATH FOUND");
                actionsList.add(MarioAction.NO_ACTION.value);
            }
            */
        }

//        System.out.println("ITERATIONS: " + iterations + " | Best X: " + furthestNode.state.getMarioX()
//            + " | Number of actions: " + actionsList.size());

        return actionsList;
    }

    private boolean isSafe(SearchNode nodeToTest) {
        if (nodeToTest.state.getWorld().mario.onGround)
            return true;
        int marioX = (int) (nodeToTest.state.getMarioX()) / 16;
        int marioY = (int) (nodeToTest.state.getMarioY()) / 16;
        var level = nodeToTest.state.getWorld().level;
        int levelHeight = MarioWorldSlim.marioGameHeight / 16;
        for (int y = marioY; y < levelHeight; y++) {
            byte block = level.getBlockValue(marioX, y);
            if (isBlockSafe(block))
                return true;
        }
        return false;
    }

    private boolean isBlockSafe(byte block) {
        return block != 0 &&  // empty
               block != 15 && // coin
               block != 47 && // background
               block != 48 && // invisible life block
               block != 49;   // invisible coin block
    }
}
