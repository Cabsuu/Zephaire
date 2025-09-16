package com.jerae.zephaire.particles.data;

import org.bukkit.entity.EntityType;

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
    private final String tag;

    public EntityTarget(TargetType targetType, EntityType entityType, String name, String tag) {
        this.targetType = targetType;
        this.entityType = entityType;
        this.name = name;
        this.tag = tag;
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

    public String getTag() {
        return tag;
    }
}
