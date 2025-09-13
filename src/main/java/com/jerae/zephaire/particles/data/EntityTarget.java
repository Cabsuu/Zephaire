package com.jerae.zephaire.particles.data;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class EntityTarget {

    public enum TargetType {
        ALL_PLAYERS,
        SPECIFIC_PLAYER,
        ALL_HOSTILE_MOBS,
        SPECIFIC_TYPE
    }

    private final TargetType targetType;
    private final EntityType entityType;
    private final String name;

    public EntityTarget(TargetType targetType, EntityType entityType, String name) {
        this.targetType = targetType;
        this.entityType = entityType;
        this.name = name;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return name;
    }
}
