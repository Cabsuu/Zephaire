package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.particles.Debuggable;
import com.jerae.zephaire.particles.ParticleRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListCommand implements SubCommand {

    private static final int PARTICLES_PER_PAGE = 10;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zephaire.list")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }

        List<String> allParticleNames = new ArrayList<>(ParticleRegistry.getParticleNames());
        Collections.sort(allParticleNames);

        if (allParticleNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No particle effects are currently loaded.");
            return;
        }

        if (args.length > 1) {
            String potentialNameOrPage = args[1];
            try {
                int page = Integer.parseInt(potentialNameOrPage);
                displayPaginatedList(sender, allParticleNames, page, "");
            } catch (NumberFormatException e) {
                // It's a particle name, not a page number.
                Optional<String> exactMatch = allParticleNames.stream()
                        .filter(name -> name.equalsIgnoreCase(potentialNameOrPage))
                        .findFirst();

                if (exactMatch.isPresent()) {
                    showDebugInfo(sender, exactMatch.get());
                    return;
                }

                // No exact match, treat as a filter
                List<String> filteredNames = allParticleNames.stream()
                        .filter(name -> name.toLowerCase().contains(potentialNameOrPage.toLowerCase()))
                        .collect(Collectors.toList());

                if (filteredNames.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No particles found matching '" + potentialNameOrPage + "'.");
                    return;
                }

                int page = 1;
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChatColor.RED + "Invalid page number '" + args[2] + "'.");
                        return;
                    }
                }
                displayPaginatedList(sender, filteredNames, page, " matching '" + potentialNameOrPage + "'");
            }
        } else {
            // No arguments, show page 1 of all particles
            displayPaginatedList(sender, allParticleNames, 1, "");
        }
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], ParticleRegistry.getParticleNames(), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "list";
    }

    private void showDebugInfo(CommandSender sender, String particleName) {
        if (!sender.hasPermission("zephaire.debug")) {
            sender.sendMessage(ChatColor.RED + "You have permission to list particles, but not to view detailed info.");
            sender.sendMessage(ChatColor.RED + "Required permission: " + ChatColor.YELLOW + "zephaire.debug");
            return;
        }

        Optional<Debuggable> particleOpt = ParticleRegistry.getParticle(particleName);
        if (particleOpt.isPresent()) {
            String debugInfo = particleOpt.get().getDebugInfo();
            sender.sendMessage(ChatColor.GOLD + "--- Debug Info for '" + particleName + "' ---");
            sender.sendMessage(debugInfo);
        } else {
            sender.sendMessage(ChatColor.RED + "Particle '" + particleName + "' not found or not running.");
        }
    }

    private void displayPaginatedList(CommandSender sender, List<String> particleNames, int page, String titleSuffix) {
        int totalPages = (int) Math.ceil((double) particleNames.size() / PARTICLES_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        if (page < 1 || page > totalPages) {
            sender.sendMessage(ChatColor.RED + "Page " + page + " does not exist. Please choose between 1 and " + totalPages + ".");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "--- Active Particles" + titleSuffix + " (Page " + page + "/" + totalPages + ") ---");

        int startIndex = (page - 1) * PARTICLES_PER_PAGE;
        int endIndex = Math.min(startIndex + PARTICLES_PER_PAGE, particleNames.size());

        if (startIndex >= particleNames.size()) {
            sender.sendMessage(ChatColor.YELLOW + "No particles to display on this page.");
            return;
        }

        for (int i = startIndex; i < endIndex; i++) {
            sender.sendMessage(ChatColor.AQUA + "- " + ChatColor.WHITE + particleNames.get(i));
        }

        if (totalPages > 1) {
            String commandBase = "/zephaire list";
            if (!titleSuffix.isEmpty()) {
                String filter = titleSuffix.substring(titleSuffix.indexOf("'") + 1, titleSuffix.lastIndexOf("'"));
                commandBase += " " + filter;
            }
            sender.sendMessage(ChatColor.GRAY + "Use " + commandBase + " <page> to see more.");
        }
    }
}
