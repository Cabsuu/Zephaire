package com.jerae.zephaire.commands;

import com.jerae.zephaire.commands.subcommands.SubCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Manages the registration and execution of subcommands.
 */
public class CommandManager {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    /**
     * Registers a new subcommand.
     *
     * @param subCommand The subcommand to register.
     */
    public void registerCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    /**
     * Retrieves a subcommand by its name.
     *
     * @param name The name of the subcommand.
     * @return An Optional containing the SubCommand if found, otherwise empty.
     */
    public Optional<SubCommand> getCommand(String name) {
        return Optional.ofNullable(subCommands.get(name.toLowerCase()));
    }

    /**
     * Gets a list of all registered subcommand names.
     *
     * @return A list of subcommand names.
     */
    public List<String> getSubCommandNames() {
        return new ArrayList<>(subCommands.keySet());
    }
}
