package com.jerae.zephaire.particles.factories.entity;

import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.animations.entity.EntityVortexParticleTask;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.factories.EntityParticleFactory;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class EntityVortexParticleFactory implements EntityParticleFactory {
    @Override
    public EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period) {
        Particle particle = ConfigValidator.getParticleType(section, "type", "PORTAL");
        double radius = ConfigValidator.getPositiveDouble(section, "radius", 3.0);
        double height = ConfigValidator.getPositiveDouble(section, "height", 5.0);
        double speed = section.getDouble("speed", 0.5);
        int particleCount = ConfigValidator.getPositiveInt(section, "particle-count", 150);
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);
        boolean spawnWhileMoving = section.getBoolean("spawn-while-moving", true);

        return new EntityVortexParticleTask(effectName, particle, radius, height, speed, particleCount, options, manager, collisionEnabled, offset, target, period, spawnWhileMoving);
    }
}
