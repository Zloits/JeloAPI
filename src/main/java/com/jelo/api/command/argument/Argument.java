package com.jelo.api.command.argument;

import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Optional;

public abstract class Argument<T> {
    private final String id;

    public Argument(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract List<String> suggest(CommandSender sender, String currentInput);

    public abstract Optional<T> parse(CommandSender sender, String input);
}