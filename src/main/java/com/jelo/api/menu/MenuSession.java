package com.jelo.api.menu;

import com.jelo.api.menu.content.MenuContent;
import com.jelo.api.menu.content.Position;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class MenuSession {

    private final Menu menu;
    private final Player player;

    private final MenuHolder holder;

    private final Map<Integer, MenuContent> renderedContents = new HashMap<>();

    private Inventory inventory;

    MenuSession(Menu menu, Player player) {
        this.menu = menu;
        this.player = player;
        this.holder = new MenuHolder(this);
    }

    /**
     * Open the inventory for player.
     */
    public void open() {
        inventory = Bukkit.createInventory(
                holder,
                menu.getInventorySize(),
                menu.getTitle()
        );
        holder.setInventory(inventory);

        render();

        player.openInventory(inventory);
        if (menu.isUseOpenSound()) {
            player.playSound(player.getLocation(), menu.getOpenSound(), 1f, 1f);
        }
    }

    /**
     * Render the inventory.
     */
    private void render() {
        renderBorder();
        renderContents();
    }

    private void renderBorder() {
        int rows = menu.getRows();
        ItemStack borderItem = new ItemStack(menu.getBorderMaterial());

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < 9; x++) {

                boolean border =
                        x == 0 ||
                                x == 8 ||
                                y == 0 ||
                                y == rows - 1;

                if (!border) {
                    continue;
                }

                int slot = inventorySlot(x, y);
                if (inventory.getItem(slot) != null) continue;

                inventory.setItem(slot, borderItem);
            }
        }
    }

    private void renderContents() {
        renderedContents.clear();

        for (MenuContent content : menu.getContents()) {
            int slot = contentSlot(content.getPosition());

            inventory.setItem(slot, content.getItemStack());
            renderedContents.put(slot, content);
        }
    }

    private int inventorySlot(int x, int y) {
        return y * 9 + x;
    }

    private int contentSlot(Position position) {
        int x = position.x();
        int y = position.y();

        if (menu.isUseBorder()) {
            x++;
            y++;
        }

        return inventorySlot(x, y);
    }

    public Optional<MenuContent> getContent(int slot) {
        return Optional.ofNullable(renderedContents.get(slot));
    }

    public Map<Integer, MenuContent> getRenderedContents() {
        return Map.copyOf(renderedContents);
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }
}
