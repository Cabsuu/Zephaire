package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.entity.Entity;

import org.bukkit.Location;

public interface EntityParticleTask extends Debuggable {
    void tick(Entity entity);
    boolean shouldCollide();
    String getEffectName();
    EntityTarget getTarget();
    ConditionManager getConditionManager();
    EntityParticleTask newInstance();
    boolean isDone();
    int getDuration();
    SpawnBehavior getSpawnBehavior();
    void setDeathLocation(Location location);

    default boolean shouldPersistOnDeath() {
        return getSpawnBehavior() == SpawnBehavior.ON_DEATH && getDuration() > 0;
    }
}
