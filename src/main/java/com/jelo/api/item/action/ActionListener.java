package com.jelo.api.item.action;

import com.jelo.api.JeloAPI;
import com.jelo.api.item.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ActionListener implements Listener {

    private final JeloAPI jeloAPI;

    public ActionListener(JeloAPI jeloAPI) {
        this.jeloAPI = jeloAPI;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Optional<CustomItem> optional = jeloAPI.getItemManager().getByItemStack(itemStack);
        if (optional.isEmpty()) return;

        CustomItem customItem = optional.get();
        ActionType actionType = ActionType.BREAK_BLOCK;

        customItem.executeAction(actionType, player, itemStack, block);
    }
}
