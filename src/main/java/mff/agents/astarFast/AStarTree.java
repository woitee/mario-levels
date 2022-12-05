package mff.agents.astarFast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.*;
import mff.agents.astarHelper.CompareByCost;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class AStarTree {
    public SearchNode furthestNode;
    public float furthestNodeDistance;

    float marioXStart;
    float marioYStart;
    float levelCurrentTime;
    int searchSteps;

    static boolean winFound = false;
    static final float maxMarioSpeedX = 10.91f;
    static float exitTileX;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCost());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();
    
    public AStarTree(MarioForwardModelSlim startState, int searchSteps) {
    	levelCurrentTime = startState.getWorld().currentTimer;
    	this.searchSteps = searchSteps;

    	marioXStart = startState.getMarioX();
    	marioYStart = startState.getMarioY();
    	
    	furthestNode = getStartNode(startState);
    	furthestNode.cost = calculateCost(startState, furthestNode.nodeDepth);
    	furthestNodeDistance = furthestNode.state.getMarioX();
    	
    	opened.add(furthestNode);
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
        //timeToFinish *= 2; // TODO: do we want weighted heuristic?
        return nodeDepth + timeToFinish;
	}
    
    public ArrayList<boolean[]> search(MarioTimerSlim timer) {
    	int iterations = 0;

        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
        	iterations++;
            SearchNode current = opened.remove();

            MarioForwardModelSlim nextState = current.state.clone();

            for (int i = 0; i < searchSteps; i++) {
                nextState.advance(current.marioAction.value);
            }

            if (!nextState.getWorld().mario.alive) {
                continue;
            }

            float nextCost = calculateCost(nextState, current.nodeDepth);
            int nextStateInt = getIntState(nextState);

            float nextStateIntOldScore = visitedStates.getOrDefault(nextStateInt, -1.0f);            
            if (nextStateIntOldScore >= 0) {
            	// WE HAVE ALREADY REACHED THIS STATE
            	if (nextCost >= nextStateIntOldScore) {
                    // AND WE DO NOT HAVE BETTER SCORE
                    continue;
                }
            }
            
            if (furthestNodeDistance < current.state.getMarioX()) {
            	furthestNode = current;
            	furthestNodeDistance = current.state.getMarioX();
            }
            
            // NEW STATE or BETTER STATE
            visitedStates.put(nextStateInt, nextCost);
            
            ArrayList<MarioAction> actions = Helper.getPossibleActions(nextState);
            for (MarioAction action : actions) {
                opened.add(getNewNode(nextState, current, nextCost, action));
            }

            if (nextState.getGameStatusCode() == 1) {
                furthestNode = current;
                //System.out.print("WIN FOUND ");
                winFound = true;
                break;
            }
        }

        ArrayList<boolean[]> actionsList = new ArrayList<>();

        SearchNode curr = furthestNode;

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionsList.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

//        System.out.println("ITERATIONS: " + iterations + " | Best X: " + furthestNode.state.getMarioX()
//            + " | Number of actions: " + actionsList.size());

        return actionsList;
    }
}
