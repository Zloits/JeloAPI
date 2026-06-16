package com.jelo.api;

import com.jelo.api.command.manager.CommandManager;
import com.jelo.api.command.manager.CommandManagerImpl;
import com.jelo.api.item.ItemManager;
import com.jelo.api.item.ItemManagerImpl;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class JeloAPI {

    public static final MiniMessage miniMessage = MiniMessage.builder().build();

    private static final ItemManager itemManager = new ItemManagerImpl();
    private static final CommandManager commandManager = new CommandManagerImpl();

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}
