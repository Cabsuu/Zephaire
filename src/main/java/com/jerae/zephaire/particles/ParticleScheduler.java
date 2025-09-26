package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParticleScheduler extends BukkitRunnable {

    private static final Queue<ParticleSpawnData> particleQueue = new ConcurrentLinkedQueue<>();
    private static final Queue<SoundPlayData> soundQueue = new ConcurrentLinkedQueue<>();
    private static Zephaire plugin;

    public static void initialize(Zephaire pluginInstance) {
        plugin = pluginInstance;
    }


    public static void queueParticle(ParticleSpawnData data) {
        particleQueue.add(data);
    }

    public static void queueSound(SoundPlayData data) {
        soundQueue.add(data);
    }

    @Override
    public void run() {
        // --- Process Particle Queue ---
        while (!particleQueue.isEmpty()) {
            ParticleSpawnData data = particleQueue.poll();
            if (data != null && data.location.getWorld() != null && data.location.isChunkLoaded()) {
                if (plugin.getDisabledWorlds().contains(data.location.getWorld().getName())) {
                    continue;
                }
                switch (data.particleType) {
                    case BUKKIT:
                        if (data.particle == Particle.SHRIEK) {
                            data.location.getWorld().spawnParticle(data.particle, data.location, 1, data.shriekDelay);
                        } else if (data.particle == Particle.VIBRATION && data.vibration != null) {
                            data.location.getWorld().spawnParticle(data.particle, data.location, 1, data.vibration);
                        } else if (data.particle == Particle.SCULK_CHARGE) {
                            try {
                                Class<?> sculkChargeClass = Class.forName("org.bukkit.Particle$SculkCharge");
                                java.lang.reflect.Constructor<?> constructor = sculkChargeClass.getConstructor(float.class);
                                Object sculkChargeOptions = constructor.newInstance((Float) data.data);
                                data.location.getWorld().spawnParticle(data.particle, data.location, 1, 0, 0, 0, 0, sculkChargeOptions);
                            } catch (Exception e) {
                                // Fallback for non-Paper servers
                                data.location.getWorld().spawnParticle(data.particle, data.location, 1);
                            }
                        } else if (data.particle == Particle.TRAIL) {
                            if (data.data instanceof java.util.Map) {
                                @SuppressWarnings("unchecked")
                                java.util.Map<String, Object> map = (java.util.Map<String, Object>) data.data;
                                Vector target = (Vector) map.get("target");
                                Particle.DustOptions dustOptions = (Particle.DustOptions) map.get("dust");

                                Location currentLocation = data.location.clone();
                                Vector direction = target.clone().subtract(currentLocation.toVector());
                                double distance = direction.length();

                                if (distance > 0) {
                                    direction.normalize().multiply(0.25); // Step vector

                                    for (double i = 0; i < distance; i += 0.25) {
                                        currentLocation.add(direction);
                                        currentLocation.getWorld().spawnParticle(Particle.DUST, currentLocation, 1, dustOptions);
                                    }
                                }
                            } else {
                                try {
                                    Class<?> trailClass = Class.forName("org.bukkit.Particle$Trail");
                                    java.lang.reflect.Constructor<?> constructor = trailClass.getConstructor(int.class);
                                    Object trailOptions = constructor.newInstance((Integer) data.data);
                                    data.location.getWorld().spawnParticle(data.particle, data.location, 1, 0, 0, 0, 0, trailOptions);
                                } catch (Exception e) {
                                    // Fallback for non-Paper servers
                                    data.location.getWorld().spawnParticle(data.particle, data.location, 1);
                                }
                            }
                        } else if (data.particle != null) {
                            data.location.getWorld().spawnParticle(
                                    data.particle, data.location, data.count,
                                    data.offsetX, data.offsetY, data.offsetZ,
                                    data.speed, data.data
                            );
                        }
                        break;
                    case VISUAL_ITEM:
                        if (data.data instanceof ItemStack) {
                            ItemStack itemStack = (ItemStack) data.data;
                            // Ensure the item has a valid material
                            if (itemStack == null || itemStack.getType().isAir()) {
                                continue;
                            }
                            Item item = data.location.getWorld().dropItem(data.location, itemStack.clone());
                            item.setPickupDelay(Integer.MAX_VALUE);
                            item.setGravity(data.hasGravity);
                            item.setVelocity(data.velocity);
                            item.setMetadata("zephaire-cosmetic", new FixedMetadataValue(plugin, true));

                            if (data.despawnTimer > 0) {
                                Bukkit.getScheduler().runTaskLater(plugin, item::remove, data.despawnTimer);
                            }
                        }
                        break;
                }
            }
        }

        // --- Process Sound Queue ---
        while (!soundQueue.isEmpty()) {
            SoundPlayData data = soundQueue.poll();
            if (data != null && data.location.isChunkLoaded()) {
                data.location.getWorld().playSound(data.location, data.sound, data.volume, data.pitch);
            }
        }
    }
}
