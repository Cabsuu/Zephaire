package com.jerae.zephaire.particles.factories.entity;

import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.animations.entity.EntityPointParticleTask;
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

public class EntityPointParticleFactory implements EntityParticleFactory {
    @Override
    public EntityParticleTask create(String effectName, ConfigurationSection section, EntityTarget target, ConditionManager manager, Vector offset, int period, SpawnBehavior spawnBehavior, int loopDelay, boolean debug, boolean inheritEntityVelocity) {
        Particle particle = ConfigValidator.getParticleType(section, "type", "FLAME");
        Object options = ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
        boolean collisionEnabled = CollisionManager.shouldCollide(section);
        int despawnTimer = section.getInt("despawn-timer", 100);
        boolean hasGravity = section.getBoolean("options.gravity", false);

        return new EntityPointParticleTask(effectName, particle, options, manager, collisionEnabled, offset, target, period, spawnBehavior, despawnTimer, hasGravity, loopDelay, debug, inheritEntityVelocity);
    }
}
