package mff.agents.robinBaumgartenSlimWindowAdvance;

import engine.helper.MarioActions;
import mff.agents.benchmark.IAgentBenchmark;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class Agent implements IMarioAgentMFF, IAgentBenchmark {
    private boolean[] action;
    private AStarTree tree;
    private int totalSearchCalls = 0;

    @Override
    public void initialize(MarioForwardModelSlim model) {
        this.action = new boolean[MarioActions.numberOfActions()];
        this.tree = new AStarTree();
    }

    @Override
    public boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer) {
        action = this.tree.optimise(model, timer);
        totalSearchCalls++;
        return action;
    }

    @Override
    public int getSearchCalls() {
        return totalSearchCalls;
    }

    @Override
    public int getNodesEvaluated() {
        return tree.nodesEvaluated;
    }

    @Override
    public String getAgentName() {
        return "Robin Baumgarten agent with slim forward model from MFF";
    }
}
