package com.jelo.api.command.condition;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface CommandCondition {

    boolean check(@NotNull CommandSender commandSender);
}
