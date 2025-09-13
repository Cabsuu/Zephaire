package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.Zephaire;
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
            sender.sendMessage(ChatColor.RED + "Usage: /zephaire toggle <particle-name>");
            return;
        }

        String particleName = args[1];

        ConfigurationSection staticSection = plugin.getConfig().getConfigurationSection("static-particles");
        ConfigurationSection animatedSection = plugin.getConfig().getConfigurationSection("animated-particles");
        boolean particleExistsInConfig = (staticSection != null && staticSection.contains(particleName)) ||
                (animatedSection != null && animatedSection.contains(particleName));

        if (!particleExistsInConfig) {
            sender.sendMessage(ChatColor.RED + "Particle effect '" + particleName + "' not found in config.yml.");
            return;
        }

        // The action is to flip the disabled state in the data file.
        // Then, we sync the runtime state to match the new data file state.
        boolean isNowEnabled = plugin.getDataManager().toggleParticle(particleName);

        if (isNowEnabled) {
            // We want it running.
            boolean wasAlreadyRunning = plugin.getParticleManager().getParticle(particleName).isPresent();
            if (wasAlreadyRunning) {
                sender.sendMessage(ChatColor.YELLOW + "Particle effect '" + particleName + "' was already enabled.");
            } else {
                // We are using enableParticle here which internally checks again if it's running,
                // this is a safe redundancy.
                boolean success = plugin.getParticleManager().enableParticle(particleName);
                if (success) {
                    sender.sendMessage(ChatColor.GREEN + "Particle effect '" + particleName + "' has been enabled.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Failed to enable particle effect '" + particleName + "'. Check server logs.");
                    // Revert data change because it failed
                    plugin.getDataManager().toggleParticle(particleName);
                }
            }
        } else {
            // We want it stopped.
            boolean wasRunning = plugin.getParticleManager().getParticle(particleName).isPresent();
            if (wasRunning) {
                plugin.getParticleManager().disableParticle(particleName);
                sender.sendMessage(ChatColor.YELLOW + "Particle effect '" + particleName + "' has been disabled.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Particle effect '" + particleName + "' was already disabled.");
            }
        }
    }


    @Override
    public List<String> getTabCompletions(String[] args) {
        if (args.length == 2) {
            // Tab-complete all particle names from the config, not just active ones
            List<String> allParticleNames = new ArrayList<>();
            ConfigurationSection staticSection = plugin.getConfig().getConfigurationSection("static-particles");
            if (staticSection != null) {
                allParticleNames.addAll(staticSection.getKeys(false));
            }
            ConfigurationSection animatedSection = plugin.getConfig().getConfigurationSection("animated-particles");
            if (animatedSection != null) {
                allParticleNames.addAll(animatedSection.getKeys(false));
            }
            return StringUtil.copyPartialMatches(args[1], allParticleNames, new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "toggle";
    }
}
