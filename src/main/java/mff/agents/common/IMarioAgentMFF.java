package mff.agents.common;

import mff.forwardmodel.slim.core.MarioForwardModelSlim;

public interface IMarioAgentMFF {

    void initialize(MarioForwardModelSlim model);

    boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer);

    String getAgentName();

}
