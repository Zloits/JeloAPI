package com.jelo.api.item.action;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ActionContext(
        @NotNull Player player,
        @NotNull ItemStack item,
        @Nullable Block block) {
}
