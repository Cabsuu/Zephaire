package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerStatCondition implements ParticleCondition {

    public enum Stat {
        HEALTH,
        HUNGER,
        EXPERIENCE
    }

    public enum Comparison {
        LESS_THAN,
        GREATER_THAN,
        EQUAL_TO
    }

    private final Stat stat;
    private final Comparison comparison;
    private final double value;

    public PlayerStatCondition(Stat stat, Comparison comparison, double value) {
        this.stat = stat;
        this.comparison = comparison;
        this.value = value;
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
            double playerValue;
            switch (stat) {
                case HEALTH:
                    playerValue = closestPlayer.getHealth();
                    break;
                case HUNGER:
                    playerValue = closestPlayer.getFoodLevel();
                    break;
                case EXPERIENCE:
                    playerValue = closestPlayer.getTotalExperience();
                    break;
                default:
                    return false;
            }

            switch (comparison) {
                case LESS_THAN:
                    return playerValue < value;
                case GREATER_THAN:
                    return playerValue > value;
                case EQUAL_TO:
                    return Math.abs(playerValue - value) < 0.001;
            }
        }

        return false;
    }
}
