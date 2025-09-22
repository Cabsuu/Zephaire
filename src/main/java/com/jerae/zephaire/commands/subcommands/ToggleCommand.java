package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.managers.ParticleGroupManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToggleCommand implements SubCommand {

    private final Zephaire plugin;

    public ToggleCommand(Zephaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zephaire.toggle")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /zephaire toggle <particle-name|group-name>");
            return;
        }

        String name = args[1];
        ParticleGroupManager groupManager = plugin.getParticleGroupManager();

        if (groupManager.isGroup(name)) {
            toggleParticleGroup(sender, name, groupManager.getParticlesInGroup(name));
        } else {
            toggleSingleParticle(sender, name);
        }
    }

    private void toggleSingleParticle(CommandSender sender, String particleName) {
        ConfigurationSection staticSection = plugin.getStaticParticlesConfig().getConfigurationSection("particles");
        ConfigurationSection animatedSection = plugin.getAnimatedParticlesConfig().getConfigurationSection("particles");

        boolean particleExists = (staticSection != null && staticSection.contains(particleName)) ||
                (animatedSection != null && animatedSection.contains(particleName));

        if (!particleExists) {
            sender.sendMessage(ChatColor.RED + "Particle effect '" + particleName + "' not found.");
            return;
        }

        boolean isNowEnabled = plugin.getDataManager().toggleParticle(particleName);

        if (isNowEnabled) {
            boolean success = plugin.getParticleManager().enableParticle(particleName);
            if (success) {
                sender.sendMessage(ChatColor.GREEN + "Particle effect '" + particleName + "' has been enabled.");
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to enable particle effect '" + particleName + "'. Check server logs.");
                plugin.getDataManager().toggleParticle(particleName); // Revert
            }
        } else {
            plugin.getParticleManager().disableParticle(particleName);
            sender.sendMessage(ChatColor.YELLOW + "Particle effect '" + particleName + "' has been disabled.");
        }
    }

    private void toggleParticleGroup(CommandSender sender, String groupName, List<String> particles) {
        if (particles.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "Particle group '" + groupName + "' is empty or does not exist.");
            return;
        }

        int enabledCount = 0;
        int disabledCount = 0;

        for (String particleName : particles) {
            boolean isNowEnabled = plugin.getDataManager().toggleParticle(particleName);
            if (isNowEnabled) {
                if (plugin.getParticleManager().enableParticle(particleName)) {
                    enabledCount++;
                }
            } else {
                plugin.getParticleManager().disableParticle(particleName);
                disabledCount++;
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Toggled group '" + groupName + "': "
                + enabledCount + " enabled, " + disabledCount + " disabled.");
    }


    @Override
    public List<String> getTabCompletions(String[] args) {
        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            // Add particle names
            ConfigurationSection staticSection = plugin.getStaticParticlesConfig().getConfigurationSection("particles");
            if (staticSection != null) {
                suggestions.addAll(staticSection.getKeys(false));
            }
            ConfigurationSection animatedSection = plugin.getAnimatedParticlesConfig().getConfigurationSection("particles");
            if (animatedSection != null) {
                suggestions.addAll(animatedSection.getKeys(false));
            }
            // Add group names
            suggestions.addAll(plugin.getParticleGroupManager().getParticleGroups().keySet());

            return StringUtil.copyPartialMatches(args[1], suggestions, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "toggle";
    }
}
