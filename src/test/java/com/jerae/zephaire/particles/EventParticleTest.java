package com.jerae.zephaire.particles;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.listeners.EntityListener;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.particles.managers.EntityParticleManager;
import com.jerae.zephaire.regions.Region;
import com.jerae.zephaire.regions.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private RegionManager regionManager;
    @Mock
    private Player player;
    @Mock
    private World world;

    private EntityListener entityListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(plugin.getEntityParticleManager()).thenReturn(entityParticleManager);
        when(plugin.getRegionManager()).thenReturn(regionManager);
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

    @Test
    public void testOnRegionEnter() {
        Location from = new Location(world, 0, 0, 0);
        Location to = new Location(world, 1, 1, 1);
        Region region = new Region("test_region", world, new org.bukkit.util.Vector(0,0,0), new org.bukkit.util.Vector(10,10,10));

        when(regionManager.getRegions(from)).thenReturn(Collections.emptyList());
        when(regionManager.getRegions(to)).thenReturn(Collections.singletonList(region));

        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        entityListener.onPlayerMove(event);

        verify(entityParticleManager, times(1)).handleEvent(player, SpawnBehavior.ON_REGION_ENTER);
    }
}
