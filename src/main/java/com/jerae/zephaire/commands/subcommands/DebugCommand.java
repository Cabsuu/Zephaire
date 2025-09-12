package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DebugCommand implements SubCommand {

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zephaire.debug")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /zephaire debug <particle-name>");
            return;
        }

        String particleName = args[1];
        Optional<Debuggable> particleOpt = ParticleRegistry.getParticle(particleName);

        if (particleOpt.isPresent()) {
            String debugInfo = particleOpt.get().getDebugInfo();
            sender.sendMessage(ChatColor.GOLD + "--- Debug Info for '" + particleName + "' ---");
            sender.sendMessage(debugInfo);
        } else {
            sender.sendMessage(ChatColor.RED + "Particle '" + particleName + "' not found or not running.");
        }
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], ParticleRegistry.getParticleNames(), new ArrayList<>());
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public String getName() {
        return "debug";
    }
}
