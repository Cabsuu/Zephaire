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
    private final Vector offset;

    public EntityTarget(TargetType targetType, EntityType entityType, String name, Vector offset) {
        this.targetType = targetType;
        this.entityType = entityType;
        this.name = name;
        this.offset = offset;
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

    public Vector getOffset() {
        return offset;
    }
}

