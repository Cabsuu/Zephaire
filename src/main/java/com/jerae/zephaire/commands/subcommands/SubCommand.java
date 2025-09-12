package com.jerae.zephaire.commands.subcommands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * An interface representing a subcommand for the /zephaire command.
 */
public interface SubCommand {

    /**
     * Executes the subcommand.
     *
     * @param sender The entity who sent the command.
     * @param args   The arguments passed to the subcommand.
     */
    void execute(CommandSender sender, String[] args);

    /**
     * Provides tab completions for the subcommand.
     *
     * @param args The current arguments typed by the sender.
     * @return A list of suggested completions.
     */
    List<String> getTabCompletions(String[] args);

    /**
     * Gets the name of the subcommand.
     *
     * @return The subcommand name.
     */
    String getName();
}
