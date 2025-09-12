package com.jerae.zephaire.particles;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractParticleTask extends BukkitRunnable implements Debuggable {

    protected String formatBoolean(boolean value) {
        return value ? ChatColor.GREEN + "true" : ChatColor.RED + "false";
    }
}