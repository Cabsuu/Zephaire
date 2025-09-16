package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.data.EntityTarget;
import org.bukkit.entity.Entity;

public interface EntityParticleTask extends Debuggable {
    void tick(Entity entity);
    boolean shouldCollide();
    String getEffectName();
    EntityTarget getTarget();
    EntityParticleTask newInstance();
}
