package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

public class DelayConditionDecorator implements ParticleCondition {

    private final ParticleCondition wrappedCondition;
    private final long delayTicks;

    private boolean isWaiting = false;
    private long delayCounter = 0;

    public DelayConditionDecorator(ParticleCondition wrappedCondition, long delayTicks) {
        this.wrappedCondition = wrappedCondition;
        this.delayTicks = delayTicks;
    }

    @Override
    public void tick() {
        wrappedCondition.tick();
    }

    @Override
    public boolean check(Location referenceLocation) {
        boolean primaryConditionMet = wrappedCondition.check(referenceLocation);

        if (primaryConditionMet && !isWaiting) {
            isWaiting = true;
            delayCounter = delayTicks;
            return false;
        }

        if (isWaiting) {
            if (!primaryConditionMet) {
                isWaiting = false;
                return false;
            }

            delayCounter--;

            if (delayCounter <= 0) {
                isWaiting = false;
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
