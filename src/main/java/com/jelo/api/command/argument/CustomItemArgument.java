package com.jelo.api.command.argument;

import com.jelo.api.JeloAPI;
import com.jelo.api.item.CustomItem;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;

public class CustomItemArgument extends Argument<CustomItem> {
    public CustomItemArgument(String id) { super(id); }

    @Override
    public Optional<CustomItem> parse(CommandSender sender, String input) {
        return JeloAPI.getItemManager().getByName(input);
    }

    @Override
    public List<String> suggest(CommandSender sender, String currentInput) {
        return JeloAPI.getItemManager().getCustomItems().stream().map(CustomItem::itemName).toList();
    }
}