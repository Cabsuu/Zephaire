package com.jerae.zephaire.particles.factories.entity;

import com.jerae.zephaire.particles.animations.LoopDelay;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.animations.entity.EntityStarParticleTask;
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

public class EntityStarParticleFactory implements EntityParticleFactory {
    @Override
    public EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period, SpawnBehavior spawnBehavior) {
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        int points = ConfigValidator.getPositiveInt(section, "points", 5);
        double outerRadius = ConfigValidator.getPositiveDouble(section, "outer-radius", 3.0);
        double innerRadius = ConfigValidator.getPositiveDouble(section, "inner-radius", 1.5);
        double speed = section.getDouble("speed", 0.02);
        double density = ConfigValidator.getPositiveDouble(section, "density", 10.0);
        double pitch = section.getDouble("pitch", 0.0);
        double yaw = section.getDouble("yaw", 0.0);
        double height = section.getDouble("height", 0.0);
        double verticalSpeed = section.getDouble("vertical-speed", 0.1);
        boolean bounce = section.getBoolean("bounce", true);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);
        int loopDelayTicks = section.getInt("loop-delay", 0);
        LoopDelay loopDelay = new LoopDelay(loopDelayTicks, System::currentTimeMillis);

        return new EntityStarParticleTask(effectName, particle, points, outerRadius, innerRadius, speed, density, options, pitch, yaw, manager, collisionEnabled, offset, target, period, height, verticalSpeed, bounce, spawnBehavior, despawnTimer, hasGravity, loopDelay);
    }
}
