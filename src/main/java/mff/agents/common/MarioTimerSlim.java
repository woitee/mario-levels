package mff.agents.common;

public class MarioTimerSlim {
    private final long timerStart;
    private final long maxTime;

    public MarioTimerSlim(long maxTime) {
        this.timerStart = System.currentTimeMillis();
        this.maxTime = maxTime;
    }

    public long getRemainingTime() {
        return Math.max(0, this.maxTime - (System.currentTimeMillis() - this.timerStart));
    }
}
