package org.gardin.foodcontrol.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class PlaceholderSupport {

    private static Method placeholderMethod;
    private static boolean lookupAttempted;

    private PlaceholderSupport() {
    }

    public static String parse(Player player, String input, String foodId) {
        String resolved = applyBuiltInPlaceholders(player, input, foodId);

        Method method = getPlaceholderMethod();
        if (method == null) {
            return resolved;
        }

        try {
            return (String) method.invoke(null, player, resolved);
        } catch (ReflectiveOperationException exception) {
            return resolved;
        }
    }

    private static String applyBuiltInPlaceholders(Player player, String input, String foodId) {
        return input
                .replace("{player}", player.getName())
                .replace("{player_name}", player.getName())
                .replace("{player_uuid}", player.getUniqueId().toString())
                .replace("{food_id}", foodId);
    }

    private static Method getPlaceholderMethod() {
        if (lookupAttempted) {
            return placeholderMethod;
        }
        lookupAttempted = true;

        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            placeholderMethod = placeholderApiClass.getMethod("setPlaceholders", org.bukkit.OfflinePlayer.class, String.class);
            return placeholderMethod;
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
