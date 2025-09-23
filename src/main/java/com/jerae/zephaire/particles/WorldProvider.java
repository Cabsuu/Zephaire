package com.jerae.zephaire.particles;

import org.bukkit.World;

@FunctionalInterface
public interface WorldProvider {
    World getWorld(String name);
}
