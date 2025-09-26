package com.jerae.zephaire.regions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Region {
    private final String name;
    private final World world;
    private final Vector min;
    private final Vector max;

    public Region(String name, World world, Vector corner1, Vector corner2) {
        this.name = name;
        this.world = world;
        this.min = new Vector(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
        this.max = new Vector(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }
        return location.getX() >= min.getX() && location.getX() <= max.getX()
                && location.getY() >= min.getY() && location.getY() <= max.getY()
                && location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }
}
