package com.jerae.zephaire.particles.animations.decorator;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.LoopDelay;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.List;

public class MultiColorTransitionDecorator implements AnimatedParticle {

    private final AnimatedParticle wrappedParticle;
    private final List<Color> colors;
    private final float size;
    private final int transitionTime;
    // --- DEFENSIVE: Cache the world object ---
    private final World world;

    private int currentColorIndex = 0;
    private int tickCounter = 0;

    public MultiColorTransitionDecorator(AnimatedParticle wrappedParticle, List<Color> colors, float size, int transitionTime) {
        if (colors.size() < 2) {
            throw new IllegalArgumentException("MultiColorTransitionDecorator requires at least 2 colors.");
        }
        this.wrappedParticle = wrappedParticle;
        this.colors = colors;
        this.size = size;
        this.transitionTime = Math.max(1, transitionTime);
        // --- DEFENSIVE: Initialize the cached world ---
        Location loc = wrappedParticle.getCurrentLocation();
        this.world = (loc != null) ? loc.getWorld() : null;
    }

    @Override
    public boolean isLoopComplete() {
        return wrappedParticle.isLoopComplete();
    }

    @Override
    public LoopDelay getLoopDelay() {
        return wrappedParticle.getLoopDelay();
    }

    @Override
    public void tick() {
        wrappedParticle.tick();

        // --- DEFENSIVE: Check if the world is still loaded ---
        if (world == null) {
            return;
        }

        tickCounter++;
        if (tickCounter >= transitionTime) {
            tickCounter = 0;
            currentColorIndex = (currentColorIndex + 1) % colors.size();
        }

        Color from = colors.get(currentColorIndex);
        Color to = colors.get((currentColorIndex + 1) % colors.size());

        Particle.DustTransition dustTransition = new Particle.DustTransition(from, to, size);

        Location loc = getCurrentLocation();
        if (loc != null) { // Chunk load check will happen in scheduler
            ParticleScheduler.queueParticle(new ParticleSpawnData(Particle.DUST_COLOR_TRANSITION, loc, 1, 0, 0, 0, 0, dustTransition));
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
    public void reset() {
        // This task is continuous, so there is nothing to reset.
    }

    @Override
    public String getDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append(ChatColor.YELLOW).append("--- Decorator: Multi-Color Transition ---").append("\n");
        info.append(ChatColor.AQUA).append("Transition Time: ").append(ChatColor.WHITE).append(transitionTime).append(" ticks\n");
        info.append(ChatColor.AQUA).append("Particle Size: ").append(ChatColor.WHITE).append(size).append("\n");
        info.append(ChatColor.AQUA).append("Color Count: ").append(ChatColor.WHITE).append(colors.size()).append("\n");
        info.append(ChatColor.DARK_AQUA).append("--- Wrapped Particle ---").append("\n");
        info.append(wrappedParticle.getDebugInfo()); // Delegate to wrapped particle
        return info.toString();
    }
}
