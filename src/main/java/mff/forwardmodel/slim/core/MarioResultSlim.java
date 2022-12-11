package mff.forwardmodel.slim.core;

import java.util.ArrayList;

import engine.core.MarioAgentEvent;
import engine.core.MarioEvent;
import engine.core.MarioForwardModel;
import engine.helper.EventType;
import engine.helper.GameStatus;
import engine.helper.SpriteType;

public class MarioResultSlim {
    private final MarioWorldSlim world;
    private final ArrayList<MarioEvent> gameEvents;
    private final ArrayList<MarioAgentEvent> agentEvents;

    public MarioResultSlim(MarioWorldSlim world, ArrayList<MarioEvent> gameEvents, ArrayList<MarioAgentEvent> agentEvents) {
        this.world = world;
        this.gameEvents = gameEvents;
        this.agentEvents = agentEvents;
    }

    public GameStatus getGameStatus() {
        switch (this.world.gameStatusCode) {
            case MarioWorldSlim.RUNNING -> {
                return GameStatus.RUNNING;
            }
            case MarioWorldSlim.WIN -> {
                return GameStatus.WIN;
            }
            case MarioWorldSlim.LOSE -> {
                return GameStatus.LOSE;
            }
            case MarioWorldSlim.TIME_OUT -> {
                return GameStatus.TIME_OUT;
            }
            default -> throw new IllegalStateException();
        }
    }

    public float getCompletionPercentage() {
        return this.world.mario.x / (this.world.level.exitTileX * 16);
    }

    public int getRemainingTime() {
        return this.world.currentTimer;
    }

    public int getMarioMode() {
        int value = 0;
        if (this.world.mario.isLarge) {
            value = 1;
        }
        if (this.world.mario.isFire) {
            value = 2;
        }
        return value;
    }

    public ArrayList<MarioEvent> getGameEvents() {
        return this.gameEvents;
    }

    public ArrayList<MarioAgentEvent> getAgentEvents() {
        return this.agentEvents;
    }

    public int getKillsTotal() {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.STOMP_KILL.getValue() || e.getEventType() == EventType.FIRE_KILL.getValue() ||
                    e.getEventType() == EventType.FALL_KILL.getValue() || e.getEventType() == EventType.SHELL_KILL.getValue()) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getKillsByFire() {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.FIRE_KILL.getValue()) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getKillsByStomp() {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.STOMP_KILL.getValue()) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getKillsByShell() {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.SHELL_KILL.getValue()) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getMarioNumKills(int enemyType) {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if ((e.getEventType() == EventType.SHELL_KILL.getValue()
                    || e.getEventType() == EventType.FIRE_KILL.getValue()
                    || e.getEventType() == EventType.STOMP_KILL.getValue()) && e.getEventParam() == enemyType) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getMarioNumHurts() {
        int hurt = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.HURT.getValue()) {
                hurt += 1;
            }
        }
        return hurt;
    }

    public int getNumBumpQuestionBlock() {
        int bump = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.BUMP.getValue() && e.getEventParam() == MarioForwardModel.OBS_QUESTION_BLOCK) {
                bump += 1;
            }
        }
        return bump;
    }

    public int getNumBumpBrick() {
        int bump = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.BUMP.getValue() && e.getEventParam() == MarioForwardModel.OBS_BRICK) {
                bump += 1;
            }
        }
        return bump;
    }

    public int getKillsByFall() {
        int kills = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.FALL_KILL.getValue()) {
                kills += 1;
            }
        }
        return kills;
    }

    public int getNumJumps() {
        int jumps = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.JUMP.getValue()) {
                jumps += 1;
            }
        }
        return jumps;
    }

    public float getMaxXJump() {
        float maxXJump = 0;
        float startX = -100;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.JUMP.getValue()) {
                startX = e.getMarioX();
            }
            if (e.getEventType() == EventType.LAND.getValue()) {
                if (Math.abs(e.getMarioX() - startX) > maxXJump) {
                    maxXJump = Math.abs(e.getMarioX() - startX);
                }
            }
        }
        return maxXJump;
    }

    public int getMaxJumpAirTime() {
        int maxAirJump = 0;
        int startTime = -100;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.JUMP.getValue()) {
                startTime = e.getTime();
            }
            if (e.getEventType() == EventType.LAND.getValue()) {
                if (e.getTime() - startTime > maxAirJump) {
                    maxAirJump = e.getTime() - startTime;
                }
            }
        }
        return maxAirJump;
    }

    public int getCurrentLives() {
        return this.world.lives;
    }

    public int getCurrentCoins() {
        return this.world.coins;
    }

    public int getNumCollectedMushrooms() {
        int collect = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.COLLECT.getValue() && e.getEventParam() == SpriteType.MUSHROOM.getValue()) {
                collect += 1;
            }
        }
        return collect;
    }

    public int getNumCollectedFireflower() {
        int collect = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.COLLECT.getValue() && e.getEventParam() == SpriteType.FIRE_FLOWER.getValue()) {
                collect += 1;
            }
        }
        return collect;
    }

    public int getNumCollectedTileCoins() {
        int collect = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.COLLECT.getValue() && e.getEventParam() == MarioForwardModel.OBS_COIN) {
                collect += 1;
            }
        }
        return collect;
    }

    public int getNumDestroyedBricks() {
        int bricks = 0;
        for (MarioEvent e : this.gameEvents) {
            if (e.getEventType() == EventType.BUMP.getValue() &&
                    e.getEventParam() == MarioForwardModel.OBS_BRICK && e.getMarioState() > 0) {
                bricks += 1;
            }
        }
        return bricks;
    }
}
