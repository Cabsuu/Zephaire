package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.VortexParticleTask;
import com.jerae.zephaire.particles.animations.LoopDelay;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class VortexParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(ConfigurationSection section, ConditionManager manager, World world, int loopDelay) {
        Location center = parseLocation(world, section, "center");
        if (center == null) {
            return null;
        }

        Particle particle = ConfigValidator.getParticleType(section, "type", "PORTAL");
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 3.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 5.0);
        double speed = section.getDouble("speed", 0.5);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 150);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);
        int loopDelayTicks = section.getInt("loop-delay", 0);
        LoopDelay loopDelay = new LoopDelay(loopDelayTicks, System::currentTimeMillis);

        return new VortexParticleTask(center, particle, radius, height, speed, particleCount, options, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
