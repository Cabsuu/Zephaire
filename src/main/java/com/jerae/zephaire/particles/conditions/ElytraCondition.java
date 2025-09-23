package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ElytraCondition implements ParticleCondition {

    public ElytraCondition() {
    }

    @Override
    public void tick() {
        // No state to update
    }

    @Override
    public boolean check(Location referenceLocation) {
        if (referenceLocation.getWorld() == null) {
            return false;
        }

        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : referenceLocation.getWorld().getPlayers()) {
            double distance = player.getLocation().distanceSquared(referenceLocation);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        if (closestPlayer != null && closestDistance < 25) { // Only check for players within 5 blocks
            return closestPlayer.isGliding();
        }

        return false;
    }
}
