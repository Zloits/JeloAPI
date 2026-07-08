package com.jelo.api.command.defaults.tests;

import com.jelo.api.command.JeloCommand;
import com.jelo.api.menu.Menu;
import com.jelo.api.menu.MenuSession;
import org.bukkit.entity.Player;

public class MenuTestOneCommand extends JeloCommand {

    public MenuTestOneCommand() {
        super("menutest-1");

        Menu menu = Menu.builder("<yellow>Test 1", 6)
                .useSoundWhenOpen(true)
                .build();

        setDefaultExecutor(((commandSender, context) -> {
            if (commandSender instanceof Player player) {
                MenuSession ignored = menu.open(player);
            } else {
                commandSender.sendMessage("no");
            }
        }));
    }
}
