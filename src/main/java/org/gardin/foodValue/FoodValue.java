package org.gardin.foodValue;

import org.bukkit.plugin.java.JavaPlugin;

public final class FoodValue extends JavaPlugin {

    private static FoodValue instance;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        getServer()
                .getPluginManager()
                .registerEvents(new FoodListener(this), this);
        // 注册命令
        getCommand("foodvalue")
                .setExecutor(new FoodCommand(this));
        getLogger().info("FoodValue enabled!");

    }
    @Override
    public void onDisable() {
        getLogger().info("FoodValue disabled!");
    }


    public static FoodValue getInstance() {
        return instance;
    }
}