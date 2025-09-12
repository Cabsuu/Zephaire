package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.World;

public class TimeCondition extends CachedParticleCondition {

    private final long from;
    private final long to;

    public TimeCondition(long from, long to) {
        super(20L); // Cache time checks for 20 ticks (1 second)
        this.from = from;
        this.to = to;
    }

    @Override
    protected boolean queryCondition(Location referenceLocation) {
        World world = referenceLocation.getWorld();
        if (world == null) return false;

        long currentTime = world.getTime();

        if (from <= to) {
            return currentTime >= from && currentTime <= to;
        } else {
            return currentTime >= from || currentTime <= to;
        }
    }
}
