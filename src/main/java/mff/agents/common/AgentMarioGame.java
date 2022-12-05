package mff.agents.common;

import engine.core.MarioForwardModel;
import engine.core.MarioRender;
import engine.core.MarioWorld;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import mff.forwardmodel.common.Converter;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;

public class AgentMarioGame {
    public static final long maxTime = 33;
    public static final long graceTime = 10;
    public static final int width = 256;
    public static final int height = 256;
    public static final boolean verbose = false;
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

    public void runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals) {
        runGame(agent, level, timer, marioState, visuals, visuals ? 30 : 0, 2);
    }

    public void runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {
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
        gameLoop(level, timer, marioState, visuals, fps);
        if (visuals)
            window.dispose();
    }

    private void gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
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

        while (world.gameStatus == GameStatus.RUNNING) {
            if (!this.pause) {

                //get actions
                agentTimer = new MarioTimerSlim(maxTime);
                boolean[] actions = this.agent.getActions(slimModel.clone(), agentTimer);

                if (verbose) {
                    if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }

                // update world
                world.update(actions);
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
        //System.out.println(world.gameStatus);
    }
}
