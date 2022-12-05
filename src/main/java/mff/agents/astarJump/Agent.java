package mff.agents.astarJump;

import mff.agents.common.IMarioAgentMFF;
import mff.agents.astarHelper.MarioAction;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import java.util.ArrayList;

public class Agent implements IMarioAgentMFF {

    private ArrayList<boolean[]> actionsList = new ArrayList<>();
    private boolean finished = false;

    @Override
    public void initialize(MarioForwardModelSlim model) {
        AStarTree.winFound = false;
    }

    @Override
    public boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer) {
        // TODO: might be improved?
        if (finished)
            return MarioAction.NO_ACTION.value;

        AStarTree tree = new AStarTree(model);
        ArrayList<boolean[]> newActionsList = tree.search(timer, 2);

        if (newActionsList != null && newActionsList.size() > actionsList.size()) {
            actionsList = newActionsList;
        }

        if (actionsList.size() == 0) { //TODO means finished?
            finished = true;
            return MarioAction.NO_ACTION.value;
        }

        return actionsList.remove(actionsList.size() - 1);
    }

    @Override
    public String getAgentName() {
        return "MFF Jumping AStar Agent";
    }
}
