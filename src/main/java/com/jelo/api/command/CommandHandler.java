package com.jelo.api.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface CommandHandler {

    void run(@NotNull CommandSender commandSender, @NotNull CommandContext context);
}
