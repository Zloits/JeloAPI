package com.jelo.api.item.defaults;

import com.jelo.api.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleItem extends CustomItem {

    @Override
    public @NotNull String itemName() {
        return "simple_item";
    }

    @Override
    public @NotNull ItemStack init() {
        return new ItemStack(Material.STICK, 1);
    }
}
