package com.jerae.zephaire.commands;

import com.jerae.zephaire.Zephaire;
import com.jerae.zephaire.commands.subcommands.DebugCommand;
import com.jerae.zephaire.commands.subcommands.ListCommand;
import com.jerae.zephaire.commands.subcommands.ReloadCommand;
import com.jerae.zephaire.commands.subcommands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ZephaireCommand implements CommandExecutor, TabCompleter {

    private final CommandManager commandManager;

    public ZephaireCommand(Zephaire plugin) {
        this.commandManager = new CommandManager();
        // Register all the subcommands
        commandManager.registerCommand(new ReloadCommand(plugin));
        commandManager.registerCommand(new DebugCommand());
        commandManager.registerCommand(new ListCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        Optional<SubCommand> subCommandOpt = commandManager.getCommand(args[0]);

        if (subCommandOpt.isPresent()) {
            subCommandOpt.get().execute(sender, args);
        } else {
            sendHelpMessage(sender);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], commandManager.getSubCommandNames(), new ArrayList<>());
        }

        if (args.length > 1) {
            Optional<SubCommand> subCommandOpt = commandManager.getCommand(args[0]);
            if (subCommandOpt.isPresent()) {
                return subCommandOpt.get().getTabCompletions(args);
            }
        }

        return Collections.emptyList();
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "--- Zephaire Plugin Help ---");
        sender.sendMessage(ChatColor.GOLD + "/zephaire reload" + ChatColor.WHITE + " - Reloads the plugin's configuration.");
        sender.sendMessage(ChatColor.GOLD + "/zephaire debug <name>" + ChatColor.WHITE + " - Shows debug info for a particle.");
        sender.sendMessage(ChatColor.GOLD + "/zephaire list [page|name]" + ChatColor.WHITE + " - Lists or inspects active particles.");
    }
}
