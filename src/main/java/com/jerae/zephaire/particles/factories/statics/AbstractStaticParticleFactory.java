package com.jerae.zephaire.particles.factories.statics;

import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.AbstractParticleFactory;
import com.jerae.zephaire.particles.factories.StaticParticleFactory;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import com.jerae.zephaire.particles.managers.CollisionManager;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractStaticParticleFactory extends AbstractParticleFactory implements StaticParticleFactory {

    @Override
    public BukkitRunnable create(ConfigurationSection section, ConditionManager manager) {
        World world = parseWorld(section);
        if (world == null) {
            return null;
        }
        return createParticleTask(section, manager, world);
    }

    protected abstract BukkitRunnable createParticleTask(ConfigurationSection section, ConditionManager manager, World world);

    protected Particle parseParticle(ConfigurationSection section) {
        return ConfigValidator.getParticleType(section, "type", "FLAME");
    }

    protected Object parseOptions(Particle particle, ConfigurationSection section) {
        return ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
    }

    protected boolean parseCollision(ConfigurationSection section) {
        return CollisionManager.shouldCollide(section);
    }
}
