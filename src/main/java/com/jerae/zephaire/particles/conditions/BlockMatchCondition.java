package com.jerae.zephaire.particles.conditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockMatchCondition implements ParticleCondition {

    private final Location location;
    private final Material material;
    private final boolean triggerOnce;

    private boolean lastState = false;

    public BlockMatchCondition(Location location, Material material, boolean triggerOnce) {
        this.location = location;
        this.material = material;
        this.triggerOnce = triggerOnce;
        this.lastState = checkBlock();
    }

    @Override
    public void tick() {
        // This condition's state is evaluated in check(), so no tick logic is needed here.
    }

    @Override
    public boolean check(Location location) {
        if (!triggerOnce) {
            return checkBlock();
        }

        boolean currentState = checkBlock();
        if (currentState && !lastState) {
            lastState = true;
            return true;
        }

        lastState = currentState;
        return false;
    }

    private boolean checkBlock() {
        if (location.getWorld() == null || !location.isChunkLoaded()) {
            return false;
        }
        Block block = location.getBlock();
        return block.getType() == material;
    }
}
