package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EntityListener implements Listener {

    private final Zephaire plugin;

    public EntityListener(Zephaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        plugin.getEntityParticleManager().checkAndApplyEffects(event.getEntity());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getEntityParticleManager().checkAndApplyEffects(event.getPlayer());
    }
}
