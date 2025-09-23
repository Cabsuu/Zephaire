package com.jerae.zephaire.particles.animations;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class AnimatedSpiralParticleTaskTest {

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
    public void testSpiralParticleSpawning() {
        // Arrange
        Location base = new Location(world, 0, 0, 0);
        ConditionManager conditionManager = new ConditionManager(Collections.emptyList());
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        AnimatedSpiralParticleTask task = new AnimatedSpiralParticleTask(base, Particle.FLAME, 1, 5, 10, 0.1, 0.1, 1, 0, null, 0, 0, false, conditionManager, false, 100, false, 0);

        // Act
        task.tick();

        // Assert
        try {
            Field queueField = ParticleScheduler.class.getDeclaredField("particleQueue");
            queueField.setAccessible(true);
            Queue<ParticleSpawnData> particleQueue = (Queue<ParticleSpawnData>) queueField.get(null);
            assertEquals(1, particleQueue.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
