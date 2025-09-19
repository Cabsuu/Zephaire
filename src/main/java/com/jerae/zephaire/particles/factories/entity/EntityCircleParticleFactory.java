package com.jerae.zephaire.particles.factories.entity;

import com.jerae.zephaire.particles.animations.entity.EntityCircleParticleTask;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class EntityCircleParticleFactory implements EntityParticleFactory {
    @Override
    public EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period, SpawnBehavior spawnBehavior, int loopDelay) {
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 20);
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 1.0);
        double speed = section.getDouble("speed", 0.1);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new EntityCircleParticleTask(effectName, particle, radius, speed, particleCount, options, pitch, yaw, manager, collisionEnabled, offset, target, period, spawnBehavior, despawnTimer, hasGravity, loopDelay);
    }
}
