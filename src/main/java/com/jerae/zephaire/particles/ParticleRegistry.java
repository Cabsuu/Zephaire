package com.jerae.zephaire.particles;

import com.jerae.zephaire.particles.conditions.AnvilCondition;
import com.jerae.zephaire.particles.conditions.BlockInteractCondition;
import com.jerae.zephaire.particles.conditions.EnchantCondition;
import com.jerae.zephaire.particles.managers.ParticleManager;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * A static facade for accessing the active ParticleManager instance.
 * This provides a convenient, centralized point of access to particle data
 * without this class being responsible for managing the data itself.
 */
public final class ParticleRegistry {

    private static ParticleManager particleManager;

    /**
     * Initializes the registry with the active ParticleManager.
     * This should only be called once by the main plugin class on enable.
     * @param manager The active ParticleManager instance.
     */
    public static void initialize(ParticleManager manager) {
        if (particleManager != null) {
            // This is a safeguard, but in a proper lifecycle, it shouldn't be necessary.
            throw new IllegalStateException("ParticleRegistry has already been initialized.");
        }
        particleManager = manager;
    }

    /**
     * Gets the names of all currently active particle effects.
     * @return An unmodifiable list of particle names.
     */
    public static List<String> getParticleNames() {
        return getManager().getParticleNames();
    }

    /**
     * Retrieves a debuggable particle effect by its name.
     * @param name The name of the particle effect.
     * @return An Optional containing the Debuggable particle if found, otherwise empty.
     */
    public static Optional<Debuggable> getParticle(String name) {
        return getManager().getParticle(name);
    }

    /**
     * Registers a condition that is triggered by player interaction with a block.
     * @param condition The BlockInteractCondition to register.
     */
    public static void registerBlockInteractCondition(BlockInteractCondition condition) {
        getManager().registerBlockInteractCondition(condition);
    }

    /**
     * Retrieves a registered BlockInteractCondition for a specific location.
     * @param location The location of the block.
     * @return An Optional containing the condition if one exists at the location, otherwise empty.
     */
    public static Optional<BlockInteractCondition> getBlockInteractConditionAt(Location location) {
        return getManager().getBlockInteractConditionAt(location);
    }

    public static void registerAnvilCondition(AnvilCondition condition) {
        getManager().registerAnvilCondition(condition);
    }

    public static Collection<AnvilCondition> getAnvilConditions() {
        return getManager().getAnvilConditions();
    }

    public static void registerEnchantCondition(EnchantCondition condition) {
        getManager().registerEnchantCondition(condition);
    }

    public static Collection<EnchantCondition> getEnchantConditions() {
        return getManager().getEnchantConditions();
    }

    /**
     * A private helper to ensure the manager is available before use.
     * @return The active ParticleManager instance.
     */
    private static ParticleManager getManager() {
        if (particleManager == null) {
            throw new IllegalStateException("ParticleRegistry has not been initialized. This is a critical error.");
        }
        return particleManager;
    }
}
