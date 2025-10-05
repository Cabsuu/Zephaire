package com.jerae.zephaire.nms;

import org.bukkit.Bukkit;

public class NMSManager {

    private static final String SERVER_VERSION = Bukkit.getServer().getBukkitVersion().split("-")[0];

    public static boolean isVersionAtLeast(String version) {
        String[] serverVersionParts = SERVER_VERSION.split("\\.");
        String[] compareVersionParts = version.split("\\.");

        int length = Math.max(serverVersionParts.length, compareVersionParts.length);
        for (int i = 0; i < length; i++) {
            int serverPart = (i < serverVersionParts.length) ? Integer.parseInt(serverVersionParts[i]) : 0;
            int comparePart = (i < compareVersionParts.length) ? Integer.parseInt(compareVersionParts[i]) : 0;

            if (serverPart > comparePart) {
                return true;
            }
            if (serverPart < comparePart) {
                return false;
            }
        }
        return true;
    }
}