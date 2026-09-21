package org.gardin.foodcontrol;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.gardin.foodcontrol.utils.PlaceholderSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodService {

    private final FoodControl plugin;

    public FoodService(FoodControl plugin) {
        this.plugin = plugin;
    }

    public FoodDefinition getFoodDefinition(String itemId) {
        return getDefinition("foods", itemId);
    }

    public FoodDefinition getCakeDefinition(String itemId) {
        return getDefinition("cakes", itemId);
    }

    public void applyFood(Player player, String foodId, int oldFood, float oldSaturation, FoodDefinition definition) {
        player.setFoodLevel(Math.min(20, oldFood + definition.nutrition()));
        player.setSaturation(Math.min(20F, oldSaturation + definition.saturation()));

        for (PotionEffect effect : definition.effects()) {
            player.addPotionEffect(effect, true);
        }

        for (String rawCommand : definition.commands()) {
            dispatchConfiguredCommand(player, foodId, rawCommand);
        }
    }

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug");
    }

    public void debug(String message) {
        if (isDebugEnabled()) {
            plugin.getLogger().info(message);
        }
    }

    private FoodDefinition getDefinition(String rootPath, String itemId) {
        String path = rootPath + "." + itemId;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null) {
            return null;
        }

        int nutrition = Math.max(0, section.getInt("nutrition"));
        float saturation = Math.max(0F, (float) section.getDouble("saturation"));
        List<PotionEffect> effects = loadEffects(rootPath, itemId, section);
        List<String> commands = section.getStringList("commands");

        return new FoodDefinition(nutrition, saturation, effects, commands);
    }

    private List<PotionEffect> loadEffects(String rootPath, String itemId, ConfigurationSection section) {
        List<PotionEffect> effects = new ArrayList<>();

        for (Map<?, ?> rawEffect : section.getMapList("effects")) {
            PotionEffect effect = toPotionEffect(rootPath, itemId, rawEffect);
            if (effect != null) {
                effects.add(effect);
            }
        }

        return effects;
    }

    private PotionEffect toPotionEffect(String rootPath, String itemId, Map<?, ?> rawEffect) {
        String typeName = asString(rawEffect.get("type"));
        if (typeName == null || typeName.isBlank()) {
            plugin.getLogger().warning(rootPath + "." + itemId + ".effects 缺少 type，已跳过该效果");
            return null;
        }

        PotionEffectType type = findPotionEffectType(typeName);
        if (type == null) {
            plugin.getLogger().warning(rootPath + "." + itemId + ".effects 使用了未知药水效果: " + typeName);
            return null;
        }

        int duration = asInt(rawEffect.get("duration"), 0);
        if (duration <= 0) {
            plugin.getLogger().warning(rootPath + "." + itemId + ".effects." + typeName + " 的 duration 必须大于 0");
            return null;
        }

        int amplifier = Math.max(0, asInt(rawEffect.get("amplifier"), 0));
        boolean ambient = asBoolean(rawEffect.get("ambient"), false);
        boolean particles = asBoolean(rawEffect.get("particles"), true);
        boolean icon = asBoolean(rawEffect.get("icon"), true);

        return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
    }

    private PotionEffectType findPotionEffectType(String rawType) {
        PotionEffectType type = PotionEffectType.getByName(rawType.toUpperCase(Locale.ROOT));
        if (type != null) {
            return type;
        }

        int separatorIndex = rawType.indexOf(':');
        if (separatorIndex >= 0 && separatorIndex < rawType.length() - 1) {
            return PotionEffectType.getByName(rawType.substring(separatorIndex + 1).toUpperCase(Locale.ROOT));
        }

        return null;
    }

    private void dispatchConfiguredCommand(Player player, String foodId, String rawCommand) {
        if (rawCommand == null || rawCommand.isBlank()) {
            return;
        }

        String command = PlaceholderSupport.parse(player, rawCommand.trim(), foodId).trim();
        if (command.isEmpty()) {
            return;
        }

        if (command.regionMatches(true, 0, "[player]", 0, 8)) {
            String playerCommand = stripLeadingSlash(command.substring(8).trim());
            if (!playerCommand.isEmpty()) {
                plugin.getServer().dispatchCommand(player, playerCommand);
            }
            return;
        }

        String consoleCommand = command;
        if (command.regionMatches(true, 0, "[console]", 0, 9)) {
            consoleCommand = command.substring(9).trim();
        }

        consoleCommand = stripLeadingSlash(consoleCommand);
        if (!consoleCommand.isEmpty()) {
            CommandSender consoleSender = plugin.getServer().getConsoleSender();
            plugin.getServer().dispatchCommand(consoleSender, consoleCommand);
        }
    }

    private String stripLeadingSlash(String command) {
        if (command.startsWith("/")) {
            return command.substring(1);
        }
        return command;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int asInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private boolean asBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof String text) {
            return Boolean.parseBoolean(text);
        }
        return defaultValue;
    }
}
