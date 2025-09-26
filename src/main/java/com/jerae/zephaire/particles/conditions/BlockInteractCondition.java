package com.jerae.zephaire.particles.conditions;

import com.jerae.zephaire.Zephaire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A condition met when a player interacts with a specific block.
 */
public class BlockInteractCondition implements ParticleCondition {

    // --- Common Properties ---
    private final Location location;
    private final Material material;
    private final Zephaire plugin;

    // --- Trigger Mode Properties ---
    private final boolean triggerOnce;
    private final long repeatDuration;
    private final Material requiredItem;
    private boolean wasTriggered = false;
    private boolean hasFired = false;
    private long activeTicksRemaining = 0;


    public BlockInteractCondition(Location location, Material material,
                                  boolean triggerOnce, long repeatDuration, Material requiredItem) {
        this.location = location;
        this.material = material;
        this.triggerOnce = triggerOnce;
        this.repeatDuration = repeatDuration;
        this.requiredItem = requiredItem;
        this.plugin = JavaPlugin.getPlugin(Zephaire.class);
    }


    @Override
    public void tick() {
        if (activeTicksRemaining > 0) {
            activeTicksRemaining--;
        }
    }

    @Override
    public boolean check(Location referenceLocation) {
        if (wasTriggered) {
            wasTriggered = false; // Consume the trigger
            return true;
        }
        return activeTicksRemaining > 0;
    }

    /**
     * Called by an event listener to trigger the non-persistent effect.
     */
    public void trigger() {
        if (triggerOnce && hasFired) return;
        if (activeTicksRemaining > 0) return; // Don't re-trigger if already active

        if (triggerOnce) {
            hasFired = true;
        }

        if (repeatDuration > 0) {
            this.activeTicksRemaining = repeatDuration;
        } else {
            this.wasTriggered = true;
        }
    }

    // --- Getters for the Listener ---
    public Location getLocation() { return location; }
    public Material getMaterial() { return material; }

    // Trigger-specific getters
    public Material getRequiredItem() { return requiredItem; }
}

