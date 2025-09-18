package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.animations.AnimatedParticle;
import com.jerae.zephaire.particles.data.EntityTarget;
import org.bukkit.entity.Entity;

public interface EntityParticleTask extends AnimatedParticle {
    void tick(Entity entity);
    String getEffectName();
    EntityTarget getTarget();
    EntityParticleTask newInstance();
}
