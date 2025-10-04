package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
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
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();

    private boolean hasMovingEffect = false;
    private boolean hasStandingStillEffect = false;


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
        lastLocations.clear();
        hasMovingEffect = false;
        hasStandingStillEffect = false;
    }

    public void startManager() {
        checkAllExistingEntities();

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        };
        animationTask.runTaskTimer(plugin, 0L, 1L);
    }

    public void addEffectTemplate(String name, EntityParticleTask task) {
        effectTemplates.put(name.toLowerCase(), task);
        if (task.getSpawnBehavior() == SpawnBehavior.MOVING) {
            hasMovingEffect = true;
        }
        if (task.getSpawnBehavior() == SpawnBehavior.STANDING_STILL) {
            hasStandingStillEffect = true;
        }
    }

    private void checkAllExistingEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                checkAndApplyEffects(entity, SpawnBehavior.ALWAYS);
            }
        }
    }

    public void checkAndApplyEffects(Entity entity, SpawnBehavior behavior) {
        for (EntityParticleTask template : effectTemplates.values()) {
            if (template.getSpawnBehavior() != behavior) {
                continue;
            }

            if (isTarget(entity, template.getTarget())) {
                Map<String, EntityParticleTask> entityEffects = activeEntityEffects.computeIfAbsent(entity.getUniqueId(), k -> new ConcurrentHashMap<>());
                entityEffects.putIfAbsent(template.getEffectName(), template.newInstance());
            }
        }
    }

    public void handleEvent(Entity entity, SpawnBehavior event) {
        for (EntityParticleTask template : effectTemplates.values()) {
            if (template.getSpawnBehavior() == event && isTarget(entity, template.getTarget())) {
                EntityParticleTask task = template.newInstance();
                if (event == SpawnBehavior.ON_DEATH) {
                    task.setDeathLocation(entity.getLocation());
                }
                Map<String, EntityParticleTask> entityEffects = activeEntityEffects.computeIfAbsent(entity.getUniqueId(), k -> new ConcurrentHashMap<>());
                // Use a unique key for each instance to allow multiple one-shot effects
                entityEffects.put(template.getEffectName() + UUID.randomUUID(), task);
            }
        }
    }

    public void removeEffects(Entity entity, SpawnBehavior behavior) {
        Map<String, EntityParticleTask> entityEffects = activeEntityEffects.get(entity.getUniqueId());
        if (entityEffects == null) {
            return;
        }
        entityEffects.values().removeIf(task -> task.getSpawnBehavior() == behavior);
    }


    private boolean isTarget(Entity entity, EntityTarget target) {
        boolean isTarget = false;
        switch (target.getTargetType()) {
            case SPECIFIC_PLAYERS:
                if (entity instanceof Player && target.getNames() != null && target.getNames().stream().anyMatch(name -> name.equalsIgnoreCase(entity.getName()))) {
                    isTarget = true;
                }
                break;
            case ALL_HOSTILE_MOBS:
                if (entity instanceof Monster) {
                    isTarget = true;
                }
                break;
            case SPECIFIC_TYPE:
                if (target.getEntityType() != null && entity.getType() == target.getEntityType()) {
                    isTarget = true;
                }
                break;
        }

        if (isTarget && target.getTag() != null && !target.getTag().isEmpty()) {
            if (!entity.getScoreboardTags().contains(target.getTag())) {
                isTarget = false;
            }
        }
        return isTarget;
    }

    void tick() {
        // Tick active effects and remove them if they are done
        for (Iterator<Map.Entry<UUID, Map<String, EntityParticleTask>>> it = activeEntityEffects.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UUID, Map<String, EntityParticleTask>> entry = it.next();
            UUID entityId = entry.getKey();
            Entity entity = Bukkit.getEntity(entityId);
            Map<String, EntityParticleTask> entityEffects = entry.getValue();

            if (entity == null || !entity.isValid()) {
                lastLocations.remove(entityId);
                // Entity is dead or invalid, handle persisting effects
                entityEffects.values().removeIf(task -> {
                    if (task.shouldPersistOnDeath()) {
                        if (task.isDone()) {
                            return true; // Remove if done
                        }
                        task.tick(null); // Tick with null entity, task will use death location
                        return false; // Keep it running
                    }
                    return true; // Remove non-persisting tasks
                });

                if (entityEffects.isEmpty()) {
                    it.remove();
                }
                continue;
            }

            for (Iterator<EntityParticleTask> taskIt = entityEffects.values().iterator(); taskIt.hasNext();) {
                EntityParticleTask task = taskIt.next();
                if (task.isDone()) {
                    taskIt.remove();
                    continue;
                }
                task.tick(entity);
            }

            if (entityEffects.isEmpty()) {
                it.remove();
            }
        }

        // Handle movement-based spawn conditions
        if (!hasMovingEffect && !hasStandingStillEffect) {
            if (!lastLocations.isEmpty()) {
                lastLocations.clear();
            }
            return;
        }

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                Location lastLocation = lastLocations.get(entity.getUniqueId());
                Location currentLocation = entity.getLocation();
                boolean isMoving = false;

                if (lastLocation != null && lastLocation.getWorld() != null && lastLocation.getWorld().equals(currentLocation.getWorld())) {
                    double dx = currentLocation.getX() - lastLocation.getX();
                    double dz = currentLocation.getZ() - lastLocation.getZ();
                    if (dx * dx + dz * dz > 0.0001) {
                        isMoving = true;
                    }
                }

                lastLocations.put(entity.getUniqueId(), currentLocation.clone());

                SpawnBehavior behavior = isMoving ? SpawnBehavior.MOVING : SpawnBehavior.STANDING_STILL;
                checkAndApplyEffects(entity, behavior);

                SpawnBehavior oppositeBehavior = isMoving ? SpawnBehavior.STANDING_STILL : SpawnBehavior.MOVING;
                removeEffects(entity, oppositeBehavior);
            }
        }

        lastLocations.entrySet().removeIf(entry -> {
            Entity entity = Bukkit.getEntity(entry.getKey());
            return entity == null || !entity.isValid();
        });
    }
}