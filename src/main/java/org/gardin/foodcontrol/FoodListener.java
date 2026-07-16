package org.gardin.foodcontrol;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.gardin.foodcontrol.utils.banCE;

public class FoodListener implements Listener {
    private final FoodControl plugin;
    public FoodListener(FoodControl plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onEat(PlayerItemConsumeEvent event){

        ItemStack item = event.getItem();
        if(banCE.isCraftEngineItem(item)){
            if(plugin.getConfig().getBoolean("debug")){
                plugin.getLogger().info(
                        "检测到 CraftEngine 物品，跳过 FoodControl"
                );
            }
            return;
        }
        Material material = item.getType();
        // 获取真实命名空间ID
        String itemId = material.getKey().toString();
        String path = "foods." + itemId;
        boolean debug =
                plugin.getConfig()
                        .getBoolean("debug");
        if(debug){
            plugin.getLogger().info("========== FoodControl Debug ==========");
            plugin.getLogger().info("Material: " + material.name());
            plugin.getLogger().info("Namespaced ID: " + itemId);
            plugin.getLogger().info("Config Path: " + path);
        }
        if(!plugin.getConfig().contains(path)){
            if(debug){plugin.getLogger().info("未找到食物配置");}
            return;
        }


        int nutrition = plugin.getConfig().getInt(path + ".nutrition");
        float saturation = (float) plugin.getConfig().getDouble(path + ".saturation");
        if(debug){
            plugin.getLogger().info("Loaded nutrition=" + nutrition
                            + " saturation="+ saturation
            );
        }
        Player player = event.getPlayer();
        int oldFood = player.getFoodLevel();
        float oldSaturation = player.getSaturation();
        plugin.getServer().getScheduler()
                .runTask(plugin, () -> {
                    // 移除原版食物效果
                    player.setFoodLevel(oldFood);
                    player.setSaturation(oldSaturation);
                    // 添加自定义效果
                    player.setFoodLevel(
                            Math.min(
                                    20,
                                    oldFood + nutrition
                            )
                    );
                    player.setSaturation(
                            Math.min(
                                    20,
                                    oldSaturation + saturation
                            )
                    );
                });

    }
}