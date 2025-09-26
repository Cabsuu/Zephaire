package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import com.jerae.zephaire.particles.conditions.ConditionManager;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
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

public class EntitySpiralParticleTaskTest {

    @Mock
    private World world;
    @Mock
    private Entity entity;
    @Mock
    private EntityTarget entityTarget;

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
    public void testEntitySpiralParticleSpawning() {
        // Arrange
        Location entityLocation = new Location(world, 0, 0, 0);
        when(entity.getLocation()).thenReturn(entityLocation);
        when(world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
        when(entity.getWorld()).thenReturn(world);

        ConditionManager conditionManager = new ConditionManager(Collections.emptyList());
        EntitySpiralParticleTask task = new EntitySpiralParticleTask("test", Particle.FLAME, 1, 5, 10, 0.1, 0.1, null, 0, 0, false, conditionManager, false, new Vector(0,0,0), entityTarget, 1, SpawnBehavior.ALWAYS, 100, false, 0, false, false, -1);

        // Act
        task.tick(entity);

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
