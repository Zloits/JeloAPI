package com.jelo.api.command.defaults;

import com.jelo.api.command.JeloCommand;

public class MainCommand extends JeloCommand {

    public MainCommand() {
        super("jeloapi", new String[]{"japi"});

        setPermission("jeloapi.command.main");
        setUsage("/jeloapi help");

        setDefaultExecutor(((commandSender, ctx) -> {
            commandSender.sendMessage("/jeloapi help");
        }));
    }
}
