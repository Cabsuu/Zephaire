package com.jerae.zephaire.listeners;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleRegistry;
import com.jerae.zephaire.particles.conditions.BlockInteractCondition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerInteractListener implements Listener {

    private final Zephaire plugin;

    public PlayerInteractListener(Zephaire plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        Location blockLocation = clickedBlock.getLocation();
        Optional<BlockInteractCondition> conditionOpt = ParticleRegistry.getBlockInteractConditionAt(blockLocation);

        conditionOpt.ifPresent(condition -> {
            if (clickedBlock.getType() == condition.getMaterial()) {
                handleTriggerInteraction(event, condition);
            }
        });
    }

    private void handleTriggerInteraction(PlayerInteractEvent event, BlockInteractCondition condition) {
        if (condition.getRequiredItem() != null) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand == null || itemInHand.getType() != condition.getRequiredItem()) {
                return; // Player is not holding the required item for a trigger.
            }
        }
        condition.trigger();
    }
}

