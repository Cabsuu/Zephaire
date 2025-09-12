package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

/**
 * An abstract base class for particle conditions that caches its result for a specific duration.
 * This is useful for conditions that don't change every tick, such as weather or time.
 */
public abstract class CachedParticleCondition implements ParticleCondition {

    private final long cacheDurationTicks;
    private long lastCheckTick = -1;
    private boolean cachedResult = false;
    private long currentTick = 0;

    public CachedParticleCondition(long cacheDurationTicks) {
        this.cacheDurationTicks = Math.max(1, cacheDurationTicks);
    }

    /**
     * The specific condition logic to be implemented by subclasses.
     * This is only called when the cache is invalid.
     * @param referenceLocation The location to check the condition at.
     * @return The result of the condition check.
     */
    protected abstract boolean queryCondition(Location referenceLocation);

    @Override
    public void tick() {
        currentTick++;
    }

    @Override
    public boolean check(Location referenceLocation) {
        if (lastCheckTick == -1 || (currentTick - lastCheckTick) >= cacheDurationTicks) {
            lastCheckTick = currentTick;
            cachedResult = queryCondition(referenceLocation);
        }
        return cachedResult;
    }
}
