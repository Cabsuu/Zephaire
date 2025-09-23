package com.jerae.zephaire.particles.statics;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class StaticConeParticleTaskTest {

    @Mock
    private World world;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Clear the particle queue before each test
        Field queueField = ParticleScheduler.class.getDeclaredField("particleQueue");
        queueField.setAccessible(true);
        Queue<ParticleSpawnData> particleQueue = (Queue<ParticleSpawnData>) queueField.get(null);
        particleQueue.clear();
    }

    @Test
    public void testConeParticleSpawning() {
        // Arrange
        Location center = new Location(world, 0, 0, 0);
        ConditionManager conditionManager = new ConditionManager(Collections.emptyList());
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        StaticConeParticleTask task = new StaticConeParticleTask(center, Particle.FLAME, 2, 3, 5, null, conditionManager, false, 100, false);

        // Act
        task.run();

        // Assert
        try {
            Field queueField = ParticleScheduler.class.getDeclaredField("particleQueue");
            queueField.setAccessible(true);
            Queue<ParticleSpawnData> particleQueue = (Queue<ParticleSpawnData>) queueField.get(null);
            assertTrue(particleQueue.size() > 100); // Check if a reasonable number of particles are spawned
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
