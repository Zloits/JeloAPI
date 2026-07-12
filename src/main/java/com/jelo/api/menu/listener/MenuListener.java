package com.jelo.api.menu.listener;

import com.jelo.api.menu.Menu;
import com.jelo.api.menu.MenuHolder;
import com.jelo.api.menu.MenuSession;
import com.jelo.api.menu.content.MenuContent;
import com.jelo.api.menu.content.click.MenuClickContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }

        MenuSession session = holder.getSession();
        Menu menu = session.getMenu();

        switch (event.getAction()) {
            case COLLECT_TO_CURSOR -> {
                event.setCancelled(!menu.isTakeable());
                return;
            }

            case MOVE_TO_OTHER_INVENTORY -> {
                event.setCancelled(!menu.isTakeable());
            }
            case HOTBAR_SWAP -> {
                event.setCancelled(true);
            }

            default -> {}
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        MenuContent content = session.getContent(event.getRawSlot()).orElse(null);

        boolean takeable = content != null && content.isTakeable() != null
                ? content.isTakeable()
                : menu.isTakeable();

        if (!takeable) {
            event.setCancelled(true);
        }

        if (content != null && content.getClickHandler() != null) {
            content.getClickHandler().handle(new MenuClickContext(
                    (Player) event.getWhoClicked(),
                    session,
                    content,
                    event
            ));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder)) {
            return;
        }

        for (int slot : event.getRawSlots()) {
            if (slot < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }

        holder.getSession().close();
    }
}
