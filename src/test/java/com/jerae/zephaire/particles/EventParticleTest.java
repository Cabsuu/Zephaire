package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.listeners.EntityListener;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class EventParticleTest {

    @Mock
    private Zephaire plugin;
    @Mock
    private EntityParticleManager entityParticleManager;
    @Mock
    private Player player;
    @Mock
    private World world;

    private EntityListener entityListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(plugin.getEntityParticleManager()).thenReturn(entityParticleManager);
        entityListener = new EntityListener(plugin);
    }

    @Test
    public void testOnDeath() {
        DamageSource damageSource = mock(DamageSource.class);
        EntityDeathEvent event = new EntityDeathEvent(player, damageSource, Collections.emptyList());
        entityListener.onEntityDeath(event);
        verify(entityParticleManager, times(1)).handleEvent(player, SpawnBehavior.ON_DEATH);
    }

    @Test
    public void testOnTeleport() {
        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 10, 10, 10);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from, to);
        entityListener.onPlayerTeleport(event);
        verify(entityParticleManager, times(1)).handleEvent(player, SpawnBehavior.ON_TELEPORT);
    }
}
