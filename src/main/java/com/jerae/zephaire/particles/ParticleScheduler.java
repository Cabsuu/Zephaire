package com.jerae.zephaire.particles;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.Sound;


public class ParticleScheduler extends BukkitRunnable {

    private static final Queue<ParticleSpawnData> particleQueue = new ConcurrentLinkedQueue<>();
    private static final Queue<SoundPlayData> soundQueue = new ConcurrentLinkedQueue<>();


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
            if (data != null && data.location.isChunkLoaded()) {
                data.location.getWorld().spawnParticle(
                        data.particle, data.location, data.count,
                        data.offsetX, data.offsetY, data.offsetZ,
                        data.speed, data.data
                );
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

