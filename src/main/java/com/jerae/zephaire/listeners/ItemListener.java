package com.jerae.zephaire.listeners;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

public class ItemListener implements Listener {
    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Item item = event.getItem();
        if (item.hasMetadata("zephaire-cosmetic")) {
            event.setCancelled(true);
        }
    }
}
