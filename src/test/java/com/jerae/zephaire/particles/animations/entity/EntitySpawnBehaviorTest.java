package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class EntitySpawnBehaviorTest {

    @Test
    public void testStandingStillSpawnBehavior() {
        Entity mockEntity = Mockito.mock(Entity.class);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", (org.bukkit.Particle) null, 0.0, 0.0, 0, (Object) null, 0.0, 0.0,
                (com.jerae.zephaire.particles.conditions.ConditionManager) null,
                false, (org.bukkit.util.Vector) null, (com.jerae.zephaire.particles.data.EntityTarget) null,
                1, SpawnBehavior.STANDING_STILL, 0, false, 0, true
        );

        // Case 1: Standing still on the ground (should spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw when standing still on ground");

        // Case 2: Moving horizontally on the ground (should not spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(1, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(true);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw when moving horizontally on ground");

        // Case 3: Standing still but in the air (should not spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(false);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw when standing still in air");
    }

    @Test
    public void testMovingSpawnBehavior() {
        Entity mockEntity = Mockito.mock(Entity.class);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", (org.bukkit.Particle) null, 0.0, 0.0, 0, (Object) null, 0.0, 0.0,
                (com.jerae.zephaire.particles.conditions.ConditionManager) null,
                false, (org.bukkit.util.Vector) null, (com.jerae.zephaire.particles.data.EntityTarget) null,
                1, SpawnBehavior.MOVING, 0, false, 0, true
        );

        // Case 1: Standing still on the ground (should not spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(true);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw when standing still on ground");

        // Case 2: Moving horizontally on the ground (should spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(1, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw when moving horizontally on ground");

        // Case 3: Standing still but in the air (should spawn)
        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(false);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw when standing still in air");
    }
}
