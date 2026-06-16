package com.jelo.api.item;

import com.jelo.api.item.ability.AbilityContext;
import com.jelo.api.item.ability.AbilityType;
import com.jelo.api.item.ability.ItemAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
