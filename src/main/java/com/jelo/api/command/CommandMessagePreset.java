package com.jelo.api.command;

import com.jelo.api.JeloAPI;
import net.kyori.adventure.text.Component;

public class CommandMessagePreset {

    public static Component NO_PERMISSION = JeloAPI.miniMessage.deserialize(
            "<red>You are not allowed to execute this command"
    );
}
