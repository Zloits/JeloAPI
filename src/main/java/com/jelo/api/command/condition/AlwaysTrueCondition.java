package com.jelo.api.command.condition;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AlwaysTrueCondition implements CommandCondition {

    @Override
    public boolean check(@NotNull CommandSender commandSender) {
        return true;
    }
}
