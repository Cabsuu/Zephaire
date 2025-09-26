package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import com.jerae.zephaire.regions.Region;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EntityListener implements Listener {

    private final Zephaire plugin;

    public EntityListener(Zephaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        plugin.getEntityParticleManager().checkAndApplyEffects(event.getEntity(), SpawnBehavior.ALWAYS);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getEntityParticleManager().checkAndApplyEffects(event.getPlayer(), SpawnBehavior.ALWAYS);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        plugin.getEntityParticleManager().handleEvent(event.getEntity(), SpawnBehavior.ON_HIT);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        plugin.getEntityParticleManager().handleEvent(event.getEntity(), SpawnBehavior.ON_DEATH);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        plugin.getEntityParticleManager().handleEvent(event.getPlayer(), SpawnBehavior.ON_TELEPORT);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        List<Region> fromRegions = plugin.getRegionManager().getRegions(from);
        List<Region> toRegions = plugin.getRegionManager().getRegions(to);

        List<String> fromRegionNames = fromRegions.stream().map(Region::getName).collect(Collectors.toList());

        for (Region toRegion : toRegions) {
            if (!fromRegionNames.contains(toRegion.getName())) {
                plugin.getEntityParticleManager().handleEvent(event.getPlayer(), SpawnBehavior.ON_REGION_ENTER);
                // We only want to trigger this once per movement, even if entering multiple regions.
                // A more advanced system might pass the region name to the event handler.
                break;
            }
        }
    }
}
