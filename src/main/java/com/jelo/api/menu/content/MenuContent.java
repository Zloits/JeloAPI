package com.jelo.api.menu.content;

import com.jelo.api.menu.content.click.MenuClickHandler;
import org.bukkit.inventory.ItemStack;

public final class MenuContent {

    private final ItemStack itemStack;
    private final Position position;

    private MenuClickHandler clickHandler;

    private Boolean takeable;

    public MenuContent(ItemStack itemStack, Position position) {
        this.itemStack = itemStack;
        this.position = position;
    }

    public static MenuContent of(ItemStack item, int x, int y) {
        return new MenuContent(item, new Position(x, y));
    }

    public MenuContent onClick(MenuClickHandler handler) {
        this.clickHandler = handler;
        return this;
    }

    public MenuContent takeable(Boolean takeable) {
        this.takeable = takeable;
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Position getPosition() {
        return position;
    }

    public Boolean isTakeable() {
        return takeable;
    }

    public MenuClickHandler getClickHandler() {
        return clickHandler;
    }
}
