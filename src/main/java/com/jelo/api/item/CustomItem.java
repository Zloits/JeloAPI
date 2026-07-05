package com.jelo.api.item;

import com.jelo.api.item.ability.AbilityContext;
import com.jelo.api.item.ability.AbilityType;
import com.jelo.api.item.ability.ItemAbility;
import com.jelo.api.item.action.ActionContext;
import com.jelo.api.item.action.ActionType;
import com.jelo.api.item.action.ItemAction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent the custom item.
 */
public abstract class CustomItem {

    /**
     * Map of all the registered abilities of the custom item.
     */
    private final Map<AbilityType, Method> abilities = new HashMap<>();

    /**
     * Map of all the registered actions of the custom item.
     */
    private final Map<ActionType, Method> actions = new HashMap<>();

    public CustomItem() {
    }

    /**
     * Get all the registered abilities of the custom item.
     *
     * @return {@link Map} of ability type and method
     */
    public final Map<AbilityType, Method> getAbilities() {
        return Map.copyOf(abilities);
    }

    /**
     * Get all the registered actions of the custom item.
     *
     * @return Map of the registered actions of the custom item
     */
    public Map<ActionType, Method> getActions() {
        return actions;
    }

    /**
     * Execute the specific type of ability for the player.
     *
     * @param type The specific ability that wants to be executed
     * @param player The specific player
     * @param item The specific custom item that player hold
     */
    public final void executeAbility(@NotNull AbilityType type, @NotNull Player player, @NotNull ItemStack item) {
        Method method = abilities.get(type);
        if (method == null) return;

        AbilityContext context = new AbilityContext(
                player,
                item
        );

        try {
            method.setAccessible(true);
            method.invoke(this, context);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registering all the abilities.
     */
    public final void registerAbilities() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(ItemAbility.class)) {
                ItemAbility itemAbility = method.getDeclaredAnnotation(ItemAbility.class);
                abilities.put(itemAbility.type(), method);
            }
        }
    }

    public final void executeAction(@NotNull ActionType type, @NotNull Player player, @NotNull ItemStack item, @Nullable Block block) {
        Method method = actions.get(type);
        if (method == null) return;

        ActionContext context = new ActionContext(
                player,
                item,
                block
        );

        try {
            method.setAccessible(true);
            method.invoke(this, context);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registering all the actions.
     */
    public final void registerActions() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(ItemAction.class)) {
                ItemAction itemAction = method.getDeclaredAnnotation(ItemAction.class);
                actions.put(itemAction.type(), method);
            }
        }
    }

    /**
     * The name of the custom item.
     */
    public abstract @NotNull String itemName();

    /**
     * The initialization of the custom item. This is called when
     * are being registered.
     */
    public abstract @NotNull ItemStack init();
}
