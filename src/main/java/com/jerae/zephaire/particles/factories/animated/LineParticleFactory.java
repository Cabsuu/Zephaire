package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.LineParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class LineParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location start = parseLocation(world, section, "start");
        Location end = parseLocation(world, section, "end");

        if (start == null || end == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);
        double speed = section.getDouble("speed", 0.05);
        boolean resetOnEnd = section.getBoolean("reset-on-end", false);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);

        return new LineParticleTask(start, end, particle, speed, period, options, resetOnEnd, manager, collisionEnabled);
    }
}
