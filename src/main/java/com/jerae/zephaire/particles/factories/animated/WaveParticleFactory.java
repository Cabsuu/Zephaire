package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.animations.WaveParticleTask;
import com.jerae.zephaire.particles.animations.LoopDelay;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class WaveParticleFactory extends AbstractAnimatedParticleFactory {
    @Override
    protected AnimatedParticle createParticleTask(ConfigurationSection section, ConditionManager manager, World world) {
        Location base = parseLocation(world, section, "base");
        if (base == null) {
            return null;
        }

        Particle particle = parseParticle(section);
        double amplitude = ConfigValidator.getPositiveDouble(section, "amplitude", 1.0);
        double length = ConfigValidator.getPositiveDouble(section, "length", 5.0);
        int period = ConfigValidator.getPositiveInt(section, "period", 1);
        double speed = section.getDouble("speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = parseOptions(particle, section);
        boolean collisionEnabled = parseCollision(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);
        int loopDelayTicks = section.getInt("loop-delay", 0);
        LoopDelay loopDelay = new LoopDelay(loopDelayTicks, System::currentTimeMillis);

        return new WaveParticleTask(base, particle, amplitude, length, speed, period, options, pitch, yaw, manager, collisionEnabled, despawnTimer, hasGravity, loopDelay);
    }
}
