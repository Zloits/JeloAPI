package com.jelo.api.command.argument;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class PlayerArgument extends Argument<Player> {

    public PlayerArgument(String id) { super(id); }

    @Override
    public List<String> suggest(CommandSender sender, String currentInput) {
        return Bukkit.getOnlinePlayers().stream().map(org.bukkit.entity.Player::getName).toList();
    }

    @Override
    public Optional<Player> parse(CommandSender sender, String input) {
        return Optional.ofNullable(org.bukkit.Bukkit.getPlayer(input));
    }
}