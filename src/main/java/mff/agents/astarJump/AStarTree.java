package mff.agents.astarJump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import mff.agents.astarHelper.CompareByCostReversed;
import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.*;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class AStarTree {
    public SearchNode bestNode;
    public float bestNodeCost;

    float marioXStart;
    float marioYStart;
    float levelCurrentTime;

    static boolean winFound = false;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCostReversed());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();
    
    public AStarTree(MarioForwardModelSlim startState) {
    	levelCurrentTime = startState.getWorld().currentTimer;
    	
    	marioXStart = startState.getMarioX();
    	marioYStart = startState.getMarioY();
    	
    	bestNode = getStartNode(startState);
    	bestNodeCost = calculateCost(startState);
    	
    	opened.add(bestNode);    		
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
    
    private float calculateCost(MarioForwardModelSlim nextState) {
        // TODO: improve?
    	int marioState = nextState.getMarioMode() * 100 + (nextState.getWorld().mario.alive ? 0 : Integer.MIN_VALUE);
		return (nextState.getMarioX() - marioXStart) * 1.5f + marioState /*+ nextState.getWorld().currentTimer / 1000.0f*/
                + (marioYStart - nextState.getMarioY());
	}
    
    public ArrayList<boolean[]> search(MarioTimerSlim timer, int searchSteps) {
    	int iterations = 0;

    	if (winFound) // TODO
    	    return null;

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

            float nextCost = calculateCost(nextState);
            int nextStateInt = getIntState(nextState);
            
            float nextStateIntOldScore = visitedStates.getOrDefault(nextStateInt, -1.0f);            
            if (nextStateIntOldScore >= 0) {
            	// WE HAVE ALREADY REACHED THIS STATE
            	if (nextCost <= nextStateIntOldScore) {
                    // AND WE DO NOT HAVE BETTER SCORE
                    continue;
                }
            }
            
            if (bestNodeCost < nextCost) { //TODO furthest?
            	bestNode = current;
            	bestNodeCost = nextCost;
            }
            
            // NEW STATE or BETTER STATE
            visitedStates.put(nextStateInt, nextCost);
            
            ArrayList<MarioAction> actions = Helper.getPossibleActions(nextState);
            for (MarioAction action : actions) {
                if (action == MarioAction.JUMP_RIGHT_SPEED)
                    opened.add(getNewNode(nextState, current, nextCost + 1, action));
                else
                    opened.add(getNewNode(nextState, current, nextCost, action));
            }

            if (nextState.getGameStatusCode() == 1) {
                //System.out.print("WIN FOUND ");
                winFound = true;
                break;
            }
        }
        
//      System.out.println("ITERATIONS: " + iterations + " / Best X: " + bestNode.state.getMarioX());

        ArrayList<boolean[]> actionsList = new ArrayList<>();

        SearchNode curr = bestNode;

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionsList.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

        return actionsList;
    }
}
