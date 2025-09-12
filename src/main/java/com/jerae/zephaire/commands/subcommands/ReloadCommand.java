package com.jerae.zephaire.commands.subcommands;

import com.jerae.zephaire.Zephaire;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements com.jerae.zephaire.commands.subcommands.SubCommand {

    private final Zephaire plugin;

    public ReloadCommand(Zephaire plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("zephaire.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return;
        }
        plugin.reloadPluginConfig();
        sender.sendMessage(ChatColor.GREEN + "Zephaire configuration has been reloaded!");
    }

    @Override
    public List<String> getTabCompletions(String[] args) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "reload";
    }
}
