package com.jerae.zephaire.particles.managers;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Location;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class EntityParticleManagerTest {

    private EntityParticleManager entityParticleManager;

    @Mock
    private Zephaire plugin;
    private static ServerMock server;
    private WorldMock world;
    private PlayerMock player;
    @Mock
    private EntityParticleTask particleTask;
    @Mock
    private EntityTarget entityTarget;

    @BeforeAll
    public static void setUpClass() {
        server = MockBukkit.mock();
    }

    @AfterAll
    public static void tearDownClass() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        world = server.addSimpleWorld("test");
        player = server.addPlayer();
        entityParticleManager = new EntityParticleManager(plugin);

        when(particleTask.getTarget()).thenReturn(entityTarget);
        when(particleTask.newInstance()).thenReturn(particleTask);
        when(particleTask.getEffectName()).thenReturn("test_effect");
        when(entityTarget.getTargetType()).thenReturn(EntityTarget.TargetType.SPECIFIC_TYPE);
        when(entityTarget.getEntityType()).thenReturn(org.bukkit.entity.EntityType.PLAYER);
    }

    @Test
    public void testStandingStillSpawnsWhenNotMoving() {
        player.setLocation(new Location(world, 0, 0, 0));
        when(particleTask.getSpawnBehavior()).thenReturn(SpawnBehavior.STANDING_STILL);
        entityParticleManager.addEffectTemplate("test_effect", particleTask);

        entityParticleManager.tick();
        entityParticleManager.tick(); // Second tick to confirm it's not moving

        verify(particleTask, atLeast(1)).tick(player);
    }

    @Test
    public void testMovingSpawnsWhenMoving() {
        when(particleTask.getSpawnBehavior()).thenReturn(SpawnBehavior.MOVING);
        entityParticleManager.addEffectTemplate("test_effect", particleTask);

        // First tick, establish location
        player.setLocation(new Location(world, 0, 0, 0));
        entityParticleManager.tick();

        // Second tick, with movement
        player.setLocation(new Location(world, 1, 0, 1));
        entityParticleManager.tick();

        // Third tick, to process the active effect
        entityParticleManager.tick();

        verify(particleTask, times(1)).tick(player);
    }

    @Test
    public void testOnDeathPersistsAfterEntityInvalid() {
        when(particleTask.getSpawnBehavior()).thenReturn(SpawnBehavior.ON_DEATH);
        when(particleTask.shouldPersistOnDeath()).thenReturn(true);
        when(particleTask.isDone()).thenReturn(false);

        entityParticleManager.addEffectTemplate("test_effect", particleTask);

        Location deathLocation = new Location(world, 0, 0, 0);
        player.setLocation(deathLocation);

        entityParticleManager.handleEvent(player, SpawnBehavior.ON_DEATH);

        player.setHealth(0); // This will make the player invalid

        // Tick for 5 ticks
        for (int i = 0; i < 5; i++) {
            entityParticleManager.tick();
        }

        verify(particleTask, times(5)).tick(null);
    }
}