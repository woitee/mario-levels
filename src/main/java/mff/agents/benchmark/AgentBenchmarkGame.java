package mff.agents.benchmark;

import engine.core.MarioForwardModel;
import engine.core.MarioRender;
import engine.core.MarioWorld;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.common.Converter;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

public class AgentBenchmarkGame {
    public static final long maxTime = 33;
    public static final int width = 256;
    public static final int height = 256;
    public boolean pause = false;

    //visualization
    private MarioRender render = null;

    private IMarioAgentMFF agent = null;

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    private void setAgent(IMarioAgentMFF agent) {
        this.agent = agent;
    }

    public AgentStats runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals) {
        return runGame(agent, level, timer, marioState, visuals, visuals ? 30 : 0, 2);
    }

    public AgentStats runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {
        JFrame window = null;
        if (visuals) {
            window = new JFrame("Mario AI Framework");
            this.render = new MarioRender(scale);
            window.setContentPane(this.render);
            window.pack();
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.render.init();
            window.setVisible(true);
        }
        this.setAgent(agent);
        AgentStats agentStats = gameLoop(level, timer, marioState, visuals, fps);
        if (visuals)
            window.dispose();
        return agentStats;
    }

    private AgentStats gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
        MarioWorld world = new MarioWorld(null);
        world.visuals = visual;
        world.initializeLevel(level, 1000 * timer);
        if (visual) {
            world.initializeVisuals(this.render.getGraphicsConfiguration());
        }
        world.mario.isLarge = marioState > 0;
        world.mario.isFire = marioState > 1;
        world.update(new boolean[MarioActions.numberOfActions()]);
        long currentTime = System.currentTimeMillis();

        //initialize graphics
        VolatileImage renderTarget = null;
        Graphics backBuffer = null;
        Graphics currentBuffer = null;
        if (visual) {
            renderTarget = this.render.createVolatileImage(width, height);
            backBuffer = this.render.getGraphics();
            currentBuffer = renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
        }

        MarioTimerSlim agentTimer;
        MarioForwardModelSlim slimModel = Converter.originalToSlim(new MarioForwardModel(world.clone()), 27);
        this.agent.initialize(slimModel);

        AgentStats agentStats = new AgentStats();

        int totalGameTicks = 0;
        long totalPlanningTime = 0;
        long runStart = System.currentTimeMillis();
        while (world.gameStatus == GameStatus.RUNNING) {
            if (!this.pause) {
                //get actions
                agentTimer = new MarioTimerSlim(maxTime);
                long planningStart = System.currentTimeMillis();
                boolean[] actions = this.agent.getActions(slimModel.clone(), agentTimer);
                long planningEnd = System.currentTimeMillis();
                totalPlanningTime += planningEnd - planningStart;

                // update world
                world.update(actions);
                totalGameTicks++;

                // keep forward model up with world
                slimModel.advance(actions);
            }

            //render world
            if (visual) {
                this.render.renderWorld(world, renderTarget, backBuffer, currentBuffer);
            }

            //check if delay needed
            if (this.getDelay(fps) > 0) {
                try {
                    currentTime += this.getDelay(fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        long runEnd = System.currentTimeMillis();
        agentStats.runTime = runEnd - runStart;
        agentStats.totalGameTicks = totalGameTicks;
        agentStats.totalPlanningTime = totalPlanningTime;
        agentStats.win = world.gameStatus == GameStatus.WIN;

        agentStats.percentageTravelled = (world.mario.x - world.level.marioTileX * 16)
                / (world.level.exitTileX * 16 - world.level.marioTileX * 16);

        try {
            IAgentBenchmark agentBenchmark = (IAgentBenchmark) this.agent;
            agentStats.searchCalls = agentBenchmark.getSearchCalls();
            agentStats.nodesEvaluated = agentBenchmark.getNodesEvaluated();
        }
        catch (ClassCastException e) {
            System.out.println("Agent doesn't implement IAgentBenchmark interface.");
        }
        return agentStats;
    }
}
