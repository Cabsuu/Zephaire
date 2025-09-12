package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.BlockInteractCondition;
import org.bukkit.Location;

import java.util.*;

/**
 * Manages the runtime state of all active particle effects.
 */
public class ParticleManager {

    private final Zephaire plugin;
    private AnimationManager animationManager;

    private final List<String> particleNames = new ArrayList<>();
    private final Map<String, Debuggable> activeParticles = new HashMap<>();
    private final Map<Location, BlockInteractCondition> blockInteractConditions = new HashMap<>();

    public ParticleManager(Zephaire plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        // Stop any existing tasks and clear all data
        if (animationManager != null) {
            try {
                animationManager.cancel();
            } catch (IllegalStateException ignored) {}
        }
        plugin.getServer().getScheduler().cancelTasks(plugin);

        particleNames.clear();
        activeParticles.clear();
        blockInteractConditions.clear();

        // Start with a fresh animation manager
        animationManager = new AnimationManager();
    }

    public void startAnimationManager() {
        if (!animationManager.getAnimatedParticles().isEmpty()) {
            animationManager.runTaskTimerAsynchronously(plugin, 0L, 1L);
        }
    }

    public void addParticle(String name, Debuggable particle) {
        particleNames.add(name);
        activeParticles.put(name, particle);
        if (particle instanceof AnimatedParticle) {
            animationManager.addAnimatedTask((AnimatedParticle) particle);
        }
    }

    public Optional<Debuggable> getParticle(String name) {
        return Optional.ofNullable(activeParticles.get(name));
    }

    public List<String> getParticleNames() {
        return Collections.unmodifiableList(particleNames);
    }

    public void registerBlockInteractCondition(BlockInteractCondition condition) {
        blockInteractConditions.put(condition.getLocation(), condition);
    }

    public Optional<BlockInteractCondition> getBlockInteractConditionAt(Location location) {
        return Optional.ofNullable(blockInteractConditions.get(location));
    }
}
