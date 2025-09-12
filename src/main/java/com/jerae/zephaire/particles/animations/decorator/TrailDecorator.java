package com.jerae.zephaire.particles.animations.decorator;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.Queue;

public class TrailDecorator implements AnimatedParticle {

    private final AnimatedParticle wrappedParticle;
    private final Particle trailParticle;
    private final Object trailParticleOptions;
    private final int trailLength;
    private final int period;
    // --- DEFENSIVE: Cache the world object ---
    private final World world;
    private int tickCounter = 0;

    private final Queue<Location> recentLocations;

    public TrailDecorator(AnimatedParticle wrappedParticle, Particle trailParticle, Object trailParticleOptions, int trailLength, int period) {
        this.wrappedParticle = wrappedParticle;
        this.trailParticle = trailParticle;
        this.trailParticleOptions = trailParticleOptions;
        this.trailLength = Math.max(1, trailLength);
        this.period = Math.max(1, period);
        this.recentLocations = new LinkedList<>();
        // --- DEFENSIVE: Initialize the cached world ---
        Location loc = wrappedParticle.getCurrentLocation();
        this.world = (loc != null) ? loc.getWorld() : null;
    }

    @Override
    public void tick() {
        wrappedParticle.tick();

        // --- DEFENSIVE: Check if the world is still loaded ---
        if (world == null) {
            return;
        }

        Location currentLoc = getCurrentLocation();
        if (currentLoc != null) {
            recentLocations.add(currentLoc.clone());
            while (recentLocations.size() > trailLength) {
                recentLocations.poll();
            }
        }

        tickCounter++;
        if (tickCounter >= period) {
            tickCounter = 0;
            if (currentLoc != null) {
                for (Location trailLoc : recentLocations) {
                    ParticleScheduler.queueParticle(new ParticleSpawnData(trailParticle, trailLoc, 1, 0, 0, 0, 0, trailParticleOptions));
                }
            }
        }
    }

    @Override
    public Location getCurrentLocation() {
        return wrappedParticle.getCurrentLocation();
    }

    @Override
    public boolean shouldCollide() {
        return wrappedParticle.shouldCollide();
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.YELLOW).append("--- Decorator: Trail ---").append("\n");
        info.append(ChatColor.AQUA).append("Trail Particle: ").append(ChatColor.WHITE).append(trailParticle.name()).append("\n");
        info.append(ChatColor.AQUA).append("Trail Length: ").append(ChatColor.WHITE).append(trailLength).append(" points\n");
        info.append(ChatColor.AQUA).append("Spawn Period: ").append(ChatColor.WHITE).append(period).append(" ticks\n");
        info.append(ChatColor.DARK_AQUA).append("--- Wrapped Particle ---").append("\n");
        info.append(wrappedParticle.getDebugInfo()); // Delegate to wrapped particle
        return info.toString();
    }
}
