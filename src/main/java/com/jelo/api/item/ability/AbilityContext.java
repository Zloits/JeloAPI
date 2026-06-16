package com.jelo.api.item.ability;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record AbilityContext(@NotNull Player player, @NotNull ItemStack item) {
}
