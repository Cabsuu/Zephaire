package com.jerae.zephaire.particles.factories.animated;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.factories.AbstractParticleFactory;
import com.jerae.zephaire.particles.factories.AnimatedParticleFactory;
import com.jerae.zephaire.particles.managers.CollisionManager;
import com.jerae.zephaire.particles.util.ConfigValidator;
import com.jerae.zephaire.particles.util.ParticleUtils;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractAnimatedParticleFactory extends AbstractParticleFactory implements AnimatedParticleFactory {

    @Override
    public AnimatedParticle create(Zephaire plugin, ConfigurationSection section, ConditionManager manager) {
        World world = parseWorld(section);
        if (world == null) {
            return null;
        }
        int loopDelay = parseLoopDelay(section);
        return createParticleTask(plugin, section, manager, world, loopDelay);
    }

    protected abstract AnimatedParticle createParticleTask(Zephaire plugin, ConfigurationSection section, ConditionManager manager, World world, int loopDelay);

    protected int parseLoopDelay(ConfigurationSection section) {
        return section.getInt("loop-delay", 0);
    }

    protected Particle parseParticle(ConfigurationSection section) {
        String particleName = section.getString("type", "FLAME").toUpperCase();
        if ("VISUAL_ITEM".equals(particleName)) {
            return null;
        }
        return ConfigValidator.getParticleType(section, "type", "FLAME");
    }

    protected Object parseOptions(Particle particle, ConfigurationSection section) {
        return ParticleUtils.parseParticleOptions(particle, section.getConfigurationSection("options"));
    }

    protected boolean parseCollision(ConfigurationSection section) {
        return CollisionManager.shouldCollide(section);
    }
}
