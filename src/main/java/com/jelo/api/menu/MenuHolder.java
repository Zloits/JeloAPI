package com.jelo.api.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NonNull;

public final class MenuHolder implements InventoryHolder {

    private final MenuSession session;
    private Inventory inventory;

    public MenuHolder(MenuSession session) {
        this.session = session;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public MenuSession getSession() {
        return session;
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }
}
