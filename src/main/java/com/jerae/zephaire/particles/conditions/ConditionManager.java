package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

import java.util.List;

public class ConditionManager {

    private final List<ParticleCondition> conditions;

    public ConditionManager(List<ParticleCondition> conditions) {
        this.conditions = conditions;
    }

    /**
     * Ticks all conditions and then checks if they are all met for a given location.
     * @param location The location to check the conditions at.
     * @return True if all conditions pass, otherwise false.
     */
    public boolean allConditionsMet(Location location) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (ParticleCondition condition : conditions) {
            condition.tick(); // Update the condition's internal state first
            if (!condition.check(location)) {
                return false;
            }
        }

        return true;
    }
}
