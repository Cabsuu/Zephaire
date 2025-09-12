package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

/**
 * An interface representing a condition that must be met for a particle to be displayed.
 */
public interface ParticleCondition {
    /**
     * Called on every tick to allow the condition to update its internal state.
     */
    void tick();

    /**
     * Checks if the condition is met at the given location.
     * @param referenceLocation The location to check the condition at (e.g., center of the effect).
     * @return True if the condition is met, otherwise false.
     */
    boolean check(Location referenceLocation);
}
