package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.CircleParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class CircleParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 20);
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 1.0);
        double speed = section.getDouble("speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new CircleParticleTask(center, particle, radius, speed, particleCount, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
