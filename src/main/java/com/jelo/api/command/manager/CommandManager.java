package com.jelo.api.command.manager;

import com.jelo.api.command.JeloCommand;
import com.jelo.api.command.argument.Argument;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface CommandManager {

    void registerCommand(@NotNull Plugin plugin, @NotNull JeloCommand command);

    void unregisterCommand(@NotNull JeloCommand command);

    void unregisterCommands();

    void registerSubCommand(@NotNull JeloCommand parentCommand, @NotNull JeloCommand childCommand);

    <T> void registerArgument(@NotNull Class<T> classType, @NotNull Function<String, Argument<?>> factory);

    <T> void unregisterArgument(@NotNull Class<T> classType);
}
