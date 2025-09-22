package com.jerae.zephaire.particles.animations.entity;

import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.jerae.zephaire.particles.ParticleScheduler;
import com.jerae.zephaire.particles.ParticleSpawnData;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.jerae.zephaire.particles.conditions.ConditionManager;


public class EntitySpawnBehaviorTest {
    private MockedStatic<ParticleScheduler> mockedScheduler;

    @BeforeEach
    public void setUp() {
        mockedScheduler = mockStatic(ParticleScheduler.class);
    }

    @AfterEach
    public void tearDown() {
        mockedScheduler.close();
    }

    @Test
    public void testStandingStillSpawnBehavior() {
        Entity mockEntity = Mockito.mock(Entity.class);
        Location mockLocation = Mockito.mock(Location.class);
        when(mockEntity.getLocation()).thenReturn(mockLocation);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(mockLocation)).thenReturn(true);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", (org.bukkit.Particle) null, 0.0, 0.0, 0, (Object) null, 0.0, 0.0,
                mockConditionManager,
                false, (org.bukkit.util.Vector) null, (com.jerae.zephaire.particles.data.EntityTarget) null,
                1, SpawnBehavior.STANDING_STILL, 0, false, 0, true, false
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
        Location mockLocation = Mockito.mock(Location.class);
        when(mockEntity.getLocation()).thenReturn(mockLocation);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(mockLocation)).thenReturn(true);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", (org.bukkit.Particle) null, 0.0, 0.0, 0, (Object) null, 0.0, 0.0,
                mockConditionManager,
                false, (org.bukkit.util.Vector) null, (com.jerae.zephaire.particles.data.EntityTarget) null,
                1, SpawnBehavior.MOVING, 0, false, 0, true, false
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

    @Test
    public void testStandingStillBehaviorWithLocationChange() {
        // This test simulates a moving entity that has a transient zero velocity.
        // For STANDING_STILL, it should NOT spawn particles if the location has changed.
        Entity mockEntity = Mockito.mock(Entity.class);
        World mockWorld = Mockito.mock(World.class);
        Location loc1 = new Location(mockWorld, 0, 0, 0);
        Location loc2 = new Location(mockWorld, 1, 0, 1); // Moved
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(loc1)).thenReturn(true);
        when(mockConditionManager.allConditionsMet(loc2)).thenReturn(true);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", null, 0.0, 0.0, 0, null, 0.0, 0.0,
                mockConditionManager, false, new Vector(0,0,0), null,
                1, SpawnBehavior.STANDING_STILL, 0, false, 0, true, false
        );

        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0)); // Zero velocity
        when(mockEntity.isOnGround()).thenReturn(true);
        when(mockEntity.getWorld()).thenReturn(mockWorld);

        // First tick
        when(mockEntity.getLocation()).thenReturn(loc1);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw on first tick for STANDING_STILL with zero velocity");

        // Second tick
        when(mockEntity.getLocation()).thenReturn(loc2);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw when location changes for STANDING_STILL");
    }

    @Test
    public void testMovingBehaviorWithLocationChange() {
        // This test simulates a moving entity that has a transient zero velocity.
        // For MOVING, it SHOULD spawn particles if the location has changed.
        Entity mockEntity = Mockito.mock(Entity.class);
        World mockWorld = Mockito.mock(World.class);
        Location loc1 = new Location(mockWorld, 0, 0, 0);
        Location loc2 = new Location(mockWorld, 1, 0, 1); // Moved
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(loc1)).thenReturn(true);
        when(mockConditionManager.allConditionsMet(loc2)).thenReturn(true);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", null, 0.0, 0.0, 0, null, 0.0, 0.0,
                mockConditionManager, false, new Vector(0,0,0), null,
                1, SpawnBehavior.MOVING, 0, false, 0, true, false
        );

        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0)); // Zero velocity
        when(mockEntity.isOnGround()).thenReturn(true);
        when(mockEntity.getWorld()).thenReturn(mockWorld);

        // First tick
        when(mockEntity.getLocation()).thenReturn(loc1);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw on first tick for MOVING with zero velocity");

        // Second tick
        when(mockEntity.getLocation()).thenReturn(loc2);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw when location changes for MOVING");
    }

    @Test
    public void testInheritEntityVelocity() {
        Entity mockEntity = Mockito.mock(Entity.class);
        World mockWorld = Mockito.mock(World.class);
        Location loc = new Location(mockWorld, 0, 0, 0);
        Vector velocity = new Vector(1, 2, 3);

        when(mockEntity.getVelocity()).thenReturn(velocity);
        when(mockEntity.isOnGround()).thenReturn(true);
        when(mockEntity.getWorld()).thenReturn(mockWorld);
        when(mockEntity.getLocation()).thenReturn(loc);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(loc)).thenReturn(true);

        EntityPointParticleTask task = new EntityPointParticleTask(
                "test", null, Mockito.mock(ItemStack.class), mockConditionManager, false, new Vector(0,0,0), null,
                1, SpawnBehavior.ALWAYS, 0, false, 0, false, true, 0.0, 1
        );

        task.tick(mockEntity);

        ArgumentCaptor<ParticleSpawnData> captor = ArgumentCaptor.forClass(ParticleSpawnData.class);
        mockedScheduler.verify(() -> ParticleScheduler.queueParticle(captor.capture()), times(1));

        ParticleSpawnData capturedData = captor.getValue();
        assertEquals(velocity, capturedData.velocity);
    }

    @Test
    public void testDoNotInheritEntityVelocity() {
        Entity mockEntity = Mockito.mock(Entity.class);
        World mockWorld = Mockito.mock(World.class);
        Location loc = new Location(mockWorld, 0, 0, 0);
        Vector velocity = new Vector(1, 2, 3);

        when(mockEntity.getVelocity()).thenReturn(velocity);
        when(mockEntity.isOnGround()).thenReturn(true);
        when(mockEntity.getWorld()).thenReturn(mockWorld);
        when(mockEntity.getLocation()).thenReturn(loc);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(loc)).thenReturn(true);

        EntityPointParticleTask task = new EntityPointParticleTask(
                "test", null, Mockito.mock(ItemStack.class), mockConditionManager, false, new Vector(0,0,0), null,
                1, SpawnBehavior.ALWAYS, 0, false, 0, false, false, 0.0, 1
        );

        task.tick(mockEntity);

        ArgumentCaptor<ParticleSpawnData> captor = ArgumentCaptor.forClass(ParticleSpawnData.class);
        mockedScheduler.verify(() -> ParticleScheduler.queueParticle(captor.capture()), times(1));

        ParticleSpawnData capturedData = captor.getValue();
        assertEquals(new Vector(0, 0, 0), capturedData.velocity);
    }

    @Test
    public void testSpread() {
        Entity mockEntity = Mockito.mock(Entity.class);
        World mockWorld = Mockito.mock(World.class);
        Location loc = new Location(mockWorld, 0, 0, 0);

        when(mockEntity.getVelocity()).thenReturn(new Vector(0, 0, 0));
        when(mockEntity.isOnGround()).thenReturn(true);
        when(mockEntity.getWorld()).thenReturn(mockWorld);
        when(mockEntity.getLocation()).thenReturn(loc);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        when(mockConditionManager.allConditionsMet(loc)).thenReturn(true);

        EntityPointParticleTask task = new EntityPointParticleTask(
                "test", null, Mockito.mock(ItemStack.class), mockConditionManager, false, new Vector(0,0,0), null,
                1, SpawnBehavior.ALWAYS, 0, false, 0, false, false, 0.5, 5
        );

        task.tick(mockEntity);

        ArgumentCaptor<ParticleSpawnData> captor = ArgumentCaptor.forClass(ParticleSpawnData.class);
        mockedScheduler.verify(() -> ParticleScheduler.queueParticle(captor.capture()), times(5));

        for (ParticleSpawnData data : captor.getAllValues()) {
            assert(data.velocity.lengthSquared() > 0);
        }
    }

    @Test
    public void testEntityParticleConditions() {
        Entity mockEntity = Mockito.mock(Entity.class);
        Location mockLocation = Mockito.mock(Location.class);
        when(mockEntity.getLocation()).thenReturn(mockLocation);
        ConditionManager mockConditionManager = Mockito.mock(ConditionManager.class);
        EntityCircleParticleTask task = new EntityCircleParticleTask(
                "test", (org.bukkit.Particle) null, 0.0, 0.0, 0, (Object) null, 0.0, 0.0,
                mockConditionManager,
                false, (org.bukkit.util.Vector) null, (com.jerae.zephaire.particles.data.EntityTarget) null,
                1, SpawnBehavior.ALWAYS, 0, false, 0, true, false
        );

        // Case 1: Conditions not met (should not spawn)
        when(mockConditionManager.allConditionsMet(mockLocation)).thenReturn(false);
        assertDoesNotThrow(() -> task.tick(mockEntity), "Should not throw when conditions are not met");

        // Case 2: Conditions met (should spawn)
        when(mockConditionManager.allConditionsMet(mockLocation)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> task.tick(mockEntity), "Should throw when conditions are met");
    }
}
