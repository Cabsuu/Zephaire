package com.jerae.zephaire.particles.managers;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.animations.entity.EntityParticleTask;
import com.jerae.zephaire.particles.data.EntityTarget;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class EntityParticleManagerTest {

    private EntityParticleManager entityParticleManager;

    @Mock
    private Zephaire plugin;
    @Mock
    private Server server;
    @Mock
    private World world;
    @Mock
    private Player player;
    @Mock
    private EntityParticleTask particleTask;
    @Mock
    private EntityTarget entityTarget;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Bukkit.setServer(server);
        when(server.getWorlds()).thenReturn(Collections.singletonList(world));
        when(world.getEntities()).thenReturn(Collections.singletonList(player));
        entityParticleManager = new EntityParticleManager(plugin);

        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getWorld()).thenReturn(world);
        when(player.isValid()).thenReturn(true);
        when(particleTask.getTarget()).thenReturn(entityTarget);
        when(particleTask.newInstance()).thenReturn(particleTask);
        when(entityTarget.getTargetType()).thenReturn(EntityTarget.TargetType.SPECIFIC_TYPE);
        when(entityTarget.getEntityType()).thenReturn(org.bukkit.entity.EntityType.PLAYER);
    }

    @Test
    public void testStandingStillSpawnsWhenNotMoving() {
        when(player.getLocation()).thenReturn(new Location(world, 0, 0, 0));
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
        when(player.getLocation()).thenReturn(new Location(world, 0, 0, 0));
        entityParticleManager.tick();

        // Second tick, with movement
        when(player.getLocation()).thenReturn(new Location(world, 1, 0, 1));
        entityParticleManager.tick();

        verify(particleTask, times(1)).tick(player);
    }

    @Test
    public void testOnDeathPersistsAfterEntityInvalid() {
        when(particleTask.getSpawnBehavior()).thenReturn(SpawnBehavior.ON_DEATH);
        when(particleTask.getDuration()).thenReturn(10);
        when(particleTask.shouldPersistOnDeath()).thenReturn(true);

        Location deathLocation = new Location(world, 0, 0, 0);
        when(player.getLocation()).thenReturn(deathLocation);

        entityParticleManager.handleEvent(player, SpawnBehavior.ON_DEATH);

        when(player.isValid()).thenReturn(false);
        when(Bukkit.getEntity(player.getUniqueId())).thenReturn(null);

        // Tick for 5 ticks
        for (int i = 0; i < 5; i++) {
            entityParticleManager.tick();
        }

        verify(particleTask, times(5)).tick(null);
    }
}