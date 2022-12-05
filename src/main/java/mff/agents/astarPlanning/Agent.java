package mff.agents.astarPlanning;

import mff.agents.common.IMarioAgentMFF;
import mff.agents.astarHelper.MarioAction;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import java.util.ArrayList;

public class Agent implements IMarioAgentMFF {

    private ArrayList<boolean[]> actionsList = new ArrayList<>();
    private AStarTree tree;
    private int ticksRemaining = 0;
    private int ticksMultiplier = 1;
    private MarioForwardModelSlim startPoint;
    private int ticksPassed = 0;
    private int startTimeTicks = 0;

    @Override
    public void initialize(MarioForwardModelSlim model) {
        startPoint = model.clone();
        AStarTree.exitTileX = model.getWorld().level.exitTileX * 16;
    }

    @Override
    public boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer) {
        if (tree != null && tree.finished) {
            if (ticksPassed == startTimeTicks) {
                if (actionsList.size() == 0)
                    return MarioAction.NO_ACTION.value;
                else
                    return actionsList.remove(actionsList.size() - 1);
            }
            else {
                ticksPassed++;
                return MarioAction.NO_ACTION.value;
            }
        }

        ticksPassed++;

        if (ticksRemaining == 0) {
            int baseTimeTicks = 10;
            ticksRemaining = baseTimeTicks * ticksMultiplier;
            startTimeTicks += ticksRemaining;
            System.out.println("Staring search with " + ticksRemaining + " ticks");
            ticksMultiplier *= 2;
            MarioForwardModelSlim futureModel = startPoint.clone();
            for (int i = 0; i < ticksRemaining; i++) {
                futureModel.advance(MarioAction.NO_ACTION.value);
            }
            tree = new AStarTree(futureModel, 2);
        }

        ticksRemaining--;

        assert tree != null;
        tree.search(timer);
        if (tree.finished) {
            System.out.println("Ticks needed: " + ticksPassed);
            actionsList = tree.getActionPlan();
        }
        return MarioAction.NO_ACTION.value;
    }

    @Override
    public String getAgentName() {
        return "MFF Planning AStar Agent";
    }
}
