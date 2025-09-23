package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.HelixParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class HelixParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location base = parseLocation(world, section, "base");
        if (base == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 1.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 5.0);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);
        double speed = section.getDouble("speed", 0.1);
        double verticalSpeed = section.getDouble("vertical-speed", 0.1);
        double startAngle = section.getDouble("start-angle", 0.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        boolean bounce = section.getBoolean("bounce", false);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new HelixParticleTask(base, particle, radius, height, speed, verticalSpeed, period, startAngle, options, pitch, yaw, bounce, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
