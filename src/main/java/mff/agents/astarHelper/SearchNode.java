package mff.agents.astarHelper;

import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public class SearchNode {
	
	public SearchNode(MarioForwardModelSlim state) {
		this.parent = null;
		this.nodeDepth = 0;
		this.cost = 0;
		this.marioAction = MarioAction.NO_ACTION;
		this.state = state;
	}
	
	public SearchNode(MarioForwardModelSlim state, SearchNode parent, float cost, MarioAction marioAction) {
		this.parent = parent;
		this.nodeDepth = parent.nodeDepth + 1;
		this.cost = cost;
		this.marioAction = marioAction;
		this.state = state;
	}

	public SearchNode parent;
	public int nodeDepth;
	
	public MarioForwardModelSlim state;
	
	public MarioAction marioAction;

	public float cost;
}
