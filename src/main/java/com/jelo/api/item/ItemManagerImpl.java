package com.jelo.api.item;

import com.jelo.api.JeloAPI;
import com.jelo.api.exception.ItemException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ItemManagerImpl implements ItemManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemManagerImpl.class);

    private final Map<String, CustomItem> customItems = new HashMap<>();
    private final Map<CustomItem, ItemStack> customItemItemStackMap = new HashMap<>();

    private final NamespacedKey namespacedKey;

    public ItemManagerImpl() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("JeloAPI");
        if (plugin == null) throw new NullPointerException("Plugin JeloAPI is not found. This is might be issues coming from the API itself.");

        this.namespacedKey = new NamespacedKey(
                plugin,
                "CUSTOM_ITEM_NAME"
        );
    }

    public Map<String, CustomItem> getCustomItemsMap() {
        return customItems;
    }

    public Map<CustomItem, ItemStack> getCustomItemItemStackMap() {
        return customItemItemStackMap;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public Collection<CustomItem> getCustomItems() {
        return Collections.unmodifiableCollection(customItems.values());
    }

    @Override
    public Optional<CustomItem> getByName(@NotNull String name) {
        return customItems.containsKey(name) ?
                Optional.of(customItems.get(name)) :
                Optional.empty();
    }

    @Override
    public Optional<CustomItem> getByItemStack(@NotNull ItemStack itemStack) {
        return customItems.values().stream().filter(customItem ->
                itemStack.getItemMeta()
                        .getPersistentDataContainer()
                        .has(namespacedKey) &&
                itemStack.getItemMeta()
                        .getPersistentDataContainer()
                        .getOrDefault(
                                namespacedKey,
                                PersistentDataType.STRING,
                                "generic")
                        .equals(customItem.itemName()))
                .findFirst();
    }

    @Override
    public void registerItem(@NotNull CustomItem customItem) {
        if (getByName(customItem.itemName()).isPresent()) {
            LOGGER.debug("Custom item: {} is already registered. (Skip)", customItem.itemName());
            return;
        }

        String itemName = customItem.itemName();

        ItemStack itemStack = customItem.init();
        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, itemName));

        customItem.registerAbilities();

        customItems.put(itemName, customItem);
        customItemItemStackMap.put(customItem, itemStack);

        LOGGER.info("Custom item: {} is successfully registered", itemName);
    }

    @Override
    public void unregisterItem(@NotNull CustomItem customItem) {
        if (getByName(customItem.itemName()).isEmpty()) {
            LOGGER.debug("Custom item: {} is not found (UNREGISTER ACTION). (Skipped)", customItem.itemName());
            return;
        }
        String itemName = customItem.itemName();
        customItems.remove(itemName, customItem);
        customItemItemStackMap.remove(customItem);

        LOGGER.info("Custom item: {} is successfully unregistered", itemName);
    }

    @Override
    public void giveItem(@NotNull Player player, @NotNull CustomItem customItem) {
        String itemName = customItem.itemName();
        ItemStack itemStack = customItemItemStackMap.get(customItem).clone();
        player.getInventory().addItem(itemStack);
        player.sendMessage(JeloAPI.miniMessage.deserialize("<green>You received <white><bold>" + itemName));

        LOGGER.info("Player: {} received {}", player.getUniqueId(), itemName);
    }
}
