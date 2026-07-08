package com.jelo.api.menu.content.click;

import com.jelo.api.menu.MenuSession;
import com.jelo.api.menu.content.MenuContent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public record MenuClickContext(
        @NotNull Player player,
        @NotNull MenuSession session,
        @NotNull MenuContent content,
        @NotNull InventoryClickEvent event
) {

}
