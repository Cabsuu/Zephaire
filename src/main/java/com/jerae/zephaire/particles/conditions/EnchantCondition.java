package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;

public class EnchantCondition implements ParticleCondition {
    private boolean wasTriggered = false;

    @Override
    public void tick() {
        // Tick logic is handled in check()
    }

    @Override
    public boolean check(Location referenceLocation) {
        if (wasTriggered) {
            wasTriggered = false; // Consume the trigger
            return true;
        }
        return false;
    }

    public void trigger() {
        this.wasTriggered = true;
    }
}
