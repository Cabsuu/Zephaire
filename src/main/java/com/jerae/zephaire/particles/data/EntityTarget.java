package com.jerae.zephaire.particles.data;

import org.bukkit.entity.EntityType;
import java.util.List;

public class EntityTarget {

    public enum TargetType {
        SPECIFIC_PLAYERS,
        ALL_HOSTILE_MOBS,
        SPECIFIC_TYPE
    }

    private final TargetType targetType;
    private final EntityType entityType;
    private final List<String> names;
    private final String tag;

    public EntityTarget(TargetType targetType, EntityType entityType, List<String> names, String tag) {
        this.targetType = targetType;
        this.entityType = entityType;
        this.names = names;
        this.tag = tag;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public List<String> getNames() {
        return names;
    }

    public String getTag() {
        return tag;
    }
}
