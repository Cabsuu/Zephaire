package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Sound;


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
                switch (data.particleType) {
                    case BUKKIT:
                        if (data.particle != null) {
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

