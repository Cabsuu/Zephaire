package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleConfigLoader;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.AnvilCondition;
import com.jerae.zephaire.particles.conditions.BlockInteractCondition;
import com.jerae.zephaire.particles.conditions.EnchantCondition;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Manages the runtime state of all active particle effects.
 */
public class ParticleManager {

    private final Zephaire plugin;
    private AnimationManager animationManager;
    private ParticleConfigLoader configLoader;

    private final List<String> particleNames = new ArrayList<>();
    private final Map<String, Debuggable> activeParticles = new HashMap<>();
    private final Map<Location, BlockInteractCondition> blockInteractConditions = new HashMap<>();
    private final List<AnvilCondition> anvilConditions = new ArrayList<>();
    private final List<EnchantCondition> enchantConditions = new ArrayList<>();

    public ParticleManager(Zephaire plugin) {
        this.plugin = plugin;
    }

    public void setConfigLoader(ParticleConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public void initialize() {
        // Stop any existing tasks and clear all data
        if (animationManager != null) {
            try {
                animationManager.cancel();
            } catch (IllegalStateException ignored) {}
        }
        // Cancel all active particle tasks, which are self-contained BukkitRunnables.
        for (Debuggable particle : new ArrayList<>(activeParticles.values())) {
            if (particle instanceof BukkitRunnable) {
                try {
                    if (!((BukkitRunnable) particle).isCancelled()) {
                        ((BukkitRunnable) particle).cancel();
                    }
                } catch (IllegalStateException ignored) {}
            }
        }

        particleNames.clear();
        activeParticles.clear();
        blockInteractConditions.clear();
        anvilConditions.clear();
        enchantConditions.clear();

        // Start with a fresh animation manager
        animationManager = new AnimationManager();
    }

    public void startAnimationManager() {
        boolean isScheduled;
        try {
            animationManager.getTaskId();
            isScheduled = true;
        } catch (IllegalStateException e) {
            isScheduled = false;
        }

        if (!isScheduled && !animationManager.getAnimatedParticles().isEmpty()) {
            animationManager.runTaskTimer(plugin, 0L, 1L);
        }
    }

    public void addParticle(String name, Debuggable particle) {
        particleNames.add(name);
        activeParticles.put(name.toLowerCase(), particle);
        if (particle instanceof AnimatedParticle) {
            animationManager.addAnimatedTask((AnimatedParticle) particle);
        }
    }

    public Optional<Debuggable> getParticle(String name) {
        return Optional.ofNullable(activeParticles.get(name.toLowerCase()));
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

    public void registerAnvilCondition(AnvilCondition condition) {
        anvilConditions.add(condition);
    }

    public Collection<AnvilCondition> getAnvilConditions() {
        return Collections.unmodifiableCollection(anvilConditions);
    }

    public void registerEnchantCondition(EnchantCondition condition) {
        enchantConditions.add(condition);
    }

    public Collection<EnchantCondition> getEnchantConditions() {
        return Collections.unmodifiableCollection(enchantConditions);
    }


    public boolean disableParticle(String name) {
        Optional<Debuggable> particleOpt = getParticle(name);
        if (particleOpt.isEmpty()) {
            return false; // Not active
        }

        Debuggable particle = particleOpt.get();

        if (particle instanceof AnimatedParticle) {
            animationManager.getAnimatedParticles().remove(particle);
        } else if (particle instanceof BukkitRunnable) {
            try {
                if (!((BukkitRunnable) particle).isCancelled()) {
                    ((BukkitRunnable) particle).cancel();
                }
            } catch (IllegalStateException ignored) {}
        }

        activeParticles.remove(name.toLowerCase());
        particleNames.removeIf(pName -> pName.equalsIgnoreCase(name));
        return true;
    }

    public boolean enableParticle(String name) {
        if (getParticle(name).isPresent()) {
            return false; // Already active
        }
        // Use the config loader to load and start the single particle
        return configLoader.loadSingleParticle(name);
    }
}
