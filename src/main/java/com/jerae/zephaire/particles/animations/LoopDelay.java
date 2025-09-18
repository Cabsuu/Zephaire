package com.jerae.zephaire.particles.animations;

public class LoopDelay {

    private final int delay;
    private final Ticker ticker;
    private long delayUntil = 0;

    public LoopDelay(int delay, Ticker ticker) {
        this.delay = delay;
        this.ticker = ticker;
    }

    public boolean isWaiting() {
        if (delay <= 0) {
            return false;
        }
        return ticker.getTime() < delayUntil;
    }

    public void start() {
        if (delay > 0) {
            this.delayUntil = ticker.getTime() + (delay * 50L);
        }
    }

    public long getDelayUntil() {
        return delayUntil;
    }
}
