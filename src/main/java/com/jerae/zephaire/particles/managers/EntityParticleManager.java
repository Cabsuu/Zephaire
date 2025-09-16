package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.data.EntityTarget;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the runtime state of all active entity-targeted particle effects.
 */
public class EntityParticleManager {

    private final Zephaire plugin;
    private BukkitRunnable animationTask;

    private final Map<String, EntityParticleTask> effectTemplates = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, EntityParticleTask>> activeEntityEffects = new ConcurrentHashMap<>();

    public EntityParticleManager(Zephaire plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        if (animationTask != null) {
            try {
                animationTask.cancel();
            } catch (IllegalStateException ignored) {}
        }
        activeEntityEffects.clear();
        effectTemplates.clear();
    }

    public void startManager() {
        checkAllExistingEntities();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        // ------------------- THE FIX IS ON THIS LINE -------------------
        // Switched from runTaskTimerAsynchronously to runTaskTimer to ensure thread safety
        animationTask.runTaskTimer(plugin, 0L, 1L);
        // -----------------------------------------------------------------
    }

    public void addEffectTemplate(String name, EntityParticleTask task) {
        effectTemplates.put(name.toLowerCase(), task);
    }

    private void checkAllExistingEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                checkAndApplyEffects(entity);
            }
        }
    }

    public void checkAndApplyEffects(Entity entity) {
        for (EntityParticleTask template : effectTemplates.values()) {
            EntityTarget target = template.getTarget();
            boolean shouldApply = false;

            switch (target.getTargetType()) {
                case ALL_PLAYERS:
                    if (entity instanceof Player) {
                        shouldApply = true;
                    }
                    break;
                case SPECIFIC_PLAYER:
                    if (entity instanceof Player && entity.getName().equalsIgnoreCase(target.getName())) {
                        shouldApply = true;
                    }
                    break;
                case ALL_HOSTILE_MOBS:
                    if (entity instanceof Monster) {
                        shouldApply = true;
                    }
                    break;
                case SPECIFIC_TYPE:
                    if (target.getEntityType() != null && entity.getType() == target.getEntityType()) {
                        shouldApply = true;
                    }
                    break;
            }

            // If a tag is specified, the entity must have it for the effect to apply.
            // This acts as an additional filter on top of the type check.
            if (shouldApply && target.getTag() != null && !target.getTag().isEmpty()) {
                if (!entity.getScoreboardTags().contains(target.getTag())) {
                    shouldApply = false;
                }
            }


            if (shouldApply) {
                Map<String, EntityParticleTask> entityEffects = activeEntityEffects.computeIfAbsent(entity.getUniqueId(), k -> new ConcurrentHashMap<>());
                entityEffects.putIfAbsent(template.getEffectName(), template.newInstance());
            }
        }
    }

    private void tick() {
        for (UUID entityId : new ArrayList<>(activeEntityEffects.keySet())) {
            Entity entity = Bukkit.getEntity(entityId);
            Map<String, EntityParticleTask> entityEffects = activeEntityEffects.get(entityId);

            if (entity == null || !entity.isValid() || entityEffects == null) {
                activeEntityEffects.remove(entityId);
                continue;
            }

            for (EntityParticleTask task : new ArrayList<>(entityEffects.values())) {
                task.tick(entity);
            }
        }
    }
}
