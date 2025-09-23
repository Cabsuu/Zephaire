package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.EnchantCondition;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantListener implements Listener {

    private final Zephaire plugin;

    public EnchantListener(Zephaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        // Trigger all enchant conditions
        for (EnchantCondition condition : ParticleRegistry.getEnchantConditions()) {
            condition.trigger();
        }
    }
}
