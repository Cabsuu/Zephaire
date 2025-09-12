package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.particles.ParticleRegistry;
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

        // Check if the particle name exists in the config file at all
        ConfigurationSection staticSection = plugin.getConfig().getConfigurationSection("static-particles");
        ConfigurationSection animatedSection = plugin.getConfig().getConfigurationSection("animated-particles");
        boolean particleExistsInConfig = (staticSection != null && staticSection.contains(particleName)) ||
                (animatedSection != null && animatedSection.contains(particleName));

        if (!particleExistsInConfig) {
            sender.sendMessage(ChatColor.RED + "Particle effect '" + particleName + "' not found in config.yml.");
            return;
        }

        // Toggle the persistent state and the runtime state
        if (plugin.getDataManager().isParticleDisabled(particleName)) {
            // It's currently disabled, so we are ENABLING it.
            plugin.getDataManager().toggleParticle(particleName); // Updates file, returns true (now enabled)
            plugin.getParticleManager().enableParticle(particleName); // Loads and starts the particle
            sender.sendMessage(ChatColor.GREEN + "Particle effect '" + particleName + "' has been enabled.");
        } else {
            // It's currently enabled, so we are DISABLING it.
            plugin.getDataManager().toggleParticle(particleName); // Updates file, returns false (now disabled)
            plugin.getParticleManager().disableParticle(particleName); // Stops and unloads the particle
            sender.sendMessage(ChatColor.YELLOW + "Particle effect '" + particleName + "' has been disabled.");
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

