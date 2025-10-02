package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.data.SpawnBehavior;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
}
