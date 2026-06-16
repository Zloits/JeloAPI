package com.jelo.api.command.manager;

import com.jelo.api.command.JeloCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface CommandManager {

    void registerCommand(@NotNull Plugin plugin, @NotNull JeloCommand command);

    void unregisterCommand(@NotNull JeloCommand command);

    void unregisterCommands();

    void registerSubCommand(@NotNull JeloCommand parentCommand, @NotNull JeloCommand childCommand);
}
