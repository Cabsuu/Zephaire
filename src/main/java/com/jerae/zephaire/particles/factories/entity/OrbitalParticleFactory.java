package com.jerae.zephaire.particles.factories.entity;

import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.animations.entity.OrbitalParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class OrbitalParticleFactory implements EntityParticleFactory {
    @Override
    public EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period, SpawnBehavior spawnBehavior, int loopDelay, boolean debug, boolean inheritEntityVelocity) {
        Particle particle = ConfigValidator.getParticleType(section, "particle", "REDSTONE");
        int orbitingParticles = section.getInt("orbiting-particles", 5);
        double radius = section.getDouble("radius", 2.0);
        double speed = section.getDouble("speed", 0.1);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        int duration = section.getInt("duration", -1);

        return new OrbitalParticleTask(effectName, particle, orbitingParticles, radius, speed, options, manager, offset, target, period, spawnBehavior, duration);
    }
}
