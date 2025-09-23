package com.jerae.zephaire.particles.data;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.configuration.ConfigurationSection;

public record ParticleCreationData(
        String key,
        String shape,
        ConfigurationSection config,
        ConditionManager condManager,
        boolean isAnimated
) {}
