package com.jerae.zephaire.particles.loaders;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.managers.FactoryManager;
import com.jerae.zephaire.particles.managers.ParticleManager;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * Handles the loading and instantiation of static particle effects.
 */
public class StaticParticleLoader {

    private final Zephaire plugin;
    private final FactoryManager factoryManager;
    private final ParticleManager particleManager;

    public StaticParticleLoader(Zephaire plugin, FactoryManager factoryManager, ParticleManager particleManager) {
        this.plugin = plugin;
        this.factoryManager = factoryManager;
        this.particleManager = particleManager;
    }

    public void load(String key, String shape, ConfigurationSection config, ConditionManager condManager) {
        Optional<StaticParticleFactory> factoryOpt = factoryManager.getStaticFactory(shape);

        if (factoryOpt.isEmpty()) {
            plugin.getLogger().warning("Unknown static shape: '" + shape + "' for key '" + key + "'. Skipping.");
            return;
        }

        BukkitRunnable task = factoryOpt.get().create(config, condManager);
        if (task instanceof Debuggable) {
            long period = config.getLong("period", 20L);
            // --- FIX: Switched to runTaskTimer to ensure thread safety with Bukkit API calls ---
            task.runTaskTimer(plugin, 0L, period);
            particleManager.addParticle(key, (Debuggable) task);
        }
    }
}

