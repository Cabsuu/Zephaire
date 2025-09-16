package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.CircleParticleTask;
import com.jerae.zephaire.particles.animations.visual.IParticleRenderer;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class CircleParticleFactory extends AbstractAnimatedParticleFactory {

    public CircleParticleFactory(Zephaire plugin) {
        super(plugin);
    }

    @Override
    public AnimatedParticle createParticle(ConfigurationSection config) {
        // Temporarily moved validation logic inside this class to resolve compiler issues.
        if (!validateRequiredKeys(config, "center", "radius", "particle-count", "speed")) {
            return null;
        }

        Location center = config.getLocation("center");
        if (center == null) {
            plugin.getLogger().warning("Invalid center location for circle particle in config section: " + config.getName());
            return null;
        }

        IParticleRenderer renderer = createRenderer(config);
        if (renderer == null) {
            // Error already logged by createRenderer
            return null;
        }

        double radius = config.getDouble("radius");
        int particleCount = config.getInt("particle-count");
        double speed = config.getDouble("speed");
        long period = config.getLong("period", 1L);
        double pitch = config.getDouble("pitch", 0.0);
        double yaw = config.getDouble("yaw", 0.0);

        CircleParticleTask task = new CircleParticleTask(renderer, center, radius, particleCount, speed, pitch, yaw);
        return new AnimatedParticle(task, period);
    }

    /**
     * Internal validation method to bypass external dependency issues.
     */
    private boolean validateRequiredKeys(ConfigurationSection config, String... requiredKeys) {
        for (String key : requiredKeys) {
            if (!config.contains(key, true)) {
                plugin.getLogger().warning("Missing required key '" + key + "' in config section: " + config.getName());
                return false;
            }
        }
        return true;
    }
}

