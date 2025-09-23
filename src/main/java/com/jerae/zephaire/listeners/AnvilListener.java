package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.AnvilCondition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class AnvilListener implements Listener {

    private final Zephaire plugin;

    public AnvilListener(Zephaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        // Trigger all anvil conditions
        for (AnvilCondition condition : ParticleRegistry.getAnvilConditions()) {
            condition.trigger();
        }
    }
}
