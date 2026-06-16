package com.jelo.api;

import com.jelo.api.command.defaults.MainCommand;
import com.jelo.api.item.ability.AbilityListener;
import com.jelo.api.item.commands.ItemManagerCommand;
import com.jelo.api.item.defaults.SimpleItem;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Initializing JeloAPI...");

        JeloAPI.getItemManager().registerItem(new SimpleItem());

        getServer().getPluginManager().registerEvents(new AbilityListener(), this);

        MainCommand mainCommand = new MainCommand(this);
        ItemManagerCommand itemManagerCommand = new ItemManagerCommand();

        JeloAPI.getCommandManager().registerCommand(this, itemManagerCommand);

        JeloAPI.getCommandManager().registerSubCommand(mainCommand, itemManagerCommand);
        JeloAPI.getCommandManager().registerCommand(this, mainCommand);

        getLogger().info("JeloAPI is successfully initialized");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        JeloAPI.getCommandManager().unregisterCommands();
    }
}
