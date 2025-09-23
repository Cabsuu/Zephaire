package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.PulsingCircleParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class PulsingCircleParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double maxRadius = ConfigValidator.getPositiveDouble(section, "max-radius", 3.0);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 50);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);
        double pulseSpeed = section.getDouble("pulse-speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        boolean expand = section.getBoolean("expand", false);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new PulsingCircleParticleTask(center, particle, maxRadius, pulseSpeed, particleCount, pitch, yaw, expand, options, manager, period, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
