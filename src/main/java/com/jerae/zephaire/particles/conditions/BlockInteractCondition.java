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
        // Tick logic is handled inside check() for simplicity.
    }

    @Override
    public boolean check(Location referenceLocation) {
        // In trigger mode, manage the internal, temporary state.
        if (wasTriggered) {
            wasTriggered = false; // Consume the trigger immediately.
            if (triggerOnce && hasFired) return false;
            hasFired = true;
            return true;
        }

        if (activeTicksRemaining > 0) {
            activeTicksRemaining--;
            if (triggerOnce) hasFired = true;
            return true;
        }
        return false;
    }

    /**
     * Called by an event listener to trigger the non-persistent effect.
     */
    public void trigger() {
        if (triggerOnce && hasFired) {
            return;
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

