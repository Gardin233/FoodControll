package org.gardin.foodcontrol;

import org.bukkit.plugin.java.JavaPlugin;

public final class FoodControl extends JavaPlugin {

    private static FoodControl instance;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        getServer()
                .getPluginManager()
                .registerEvents(new FoodListener(this), this);
        // 注册命令
        getCommand("foodControl")
                .setExecutor(new FoodCommand(this));
        getLogger().info("foodControl enabled!");

    }
    @Override
    public void onDisable() {
        getLogger().info("foodControl disabled!");
    }


    public static FoodControl getInstance() {
        return instance;
    }
}