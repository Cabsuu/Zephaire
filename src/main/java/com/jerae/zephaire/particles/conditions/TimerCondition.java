package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

public class TimerCondition implements ParticleCondition {

    private final long activeDuration;
    private final long cooldownDuration;
    private final long totalCycleDuration;
    private long tickCounter = 0;

    public TimerCondition(long activeDuration, long cooldownDuration) {
        this.activeDuration = Math.max(1, activeDuration);
        this.cooldownDuration = Math.max(0, cooldownDuration);
        this.totalCycleDuration = this.activeDuration + this.cooldownDuration;
    }

    @Override
    public void tick() {
        // The tick logic is handled within the check method for this specific condition.
    }

    @Override
    public boolean check(Location referenceLocation) {
        tickCounter++;

        if (tickCounter > totalCycleDuration) {
            tickCounter = 1;
        }

        return tickCounter <= activeDuration;
    }
}
