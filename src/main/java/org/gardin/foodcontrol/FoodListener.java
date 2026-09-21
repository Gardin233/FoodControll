package org.gardin.foodcontrol;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.gardin.foodcontrol.utils.banCE;

public class FoodListener implements Listener {

    private final FoodControl plugin;
    private final FoodService foodService;

    public FoodListener(FoodControl plugin) {
        this.plugin = plugin;
        this.foodService = new FoodService(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        String itemId = resolveFoodItemId(item);
        if (itemId == null) {
            return;
        }
        FoodDefinition definition = foodService.getFoodDefinition(itemId);

        foodService.debug("========== FoodControl Debug ==========");
        foodService.debug("Material: " + item.getType().name());
        foodService.debug("Namespaced ID: " + itemId);

        if (definition == null) {
            foodService.debug("未找到食物配置");
            return;
        }

        Player player = event.getPlayer();
        int oldFood = player.getFoodLevel();
        float oldSaturation = player.getSaturation();
        foodService.debug("Loaded nutrition=" + definition.nutrition()
                + " saturation=" + definition.saturation());

        plugin.getServer().getScheduler().runTask(plugin, () ->
                foodService.applyFood(player, itemId, oldFood, oldSaturation, definition));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCakeEat(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.CAKE) {
            return;
        }

        BlockData blockData = clickedBlock.getBlockData();
        if (!(blockData instanceof Cake cakeData)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getFoodLevel() >= 20) {
            return;
        }

        String itemId = Material.CAKE.getKey().toString();
        FoodDefinition definition = foodService.getCakeDefinition(itemId);
        if (definition == null) {
            foodService.debug("未找到蛋糕配置: cakes." + itemId);
            return;
        }

        int oldFood = player.getFoodLevel();
        float oldSaturation = player.getSaturation();
        int previousBites = cakeData.getBites();
        Block cakeBlock = clickedBlock;

        foodService.debug("检测到蛋糕进食，准备应用配置: " + itemId);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Block currentBlock = cakeBlock.getLocation().getBlock();
            if (!didPlayerEatCake(currentBlock, previousBites)) {
                return;
            }

            foodService.applyFood(player, itemId, oldFood, oldSaturation, definition);
        });
    }

    private boolean didPlayerEatCake(Block currentBlock, int previousBites) {
        if (currentBlock.getType() == Material.AIR) {
            return true;
        }

        if (!(currentBlock.getBlockData() instanceof Cake updatedCake)) {
            return false;
        }

        return updatedCake.getBites() > previousBites;
    }

    private String resolveFoodItemId(ItemStack item) {
        String ceConfigId = banCE.getCraftEngineConfigId(item);
        if (ceConfigId != null) {
            foodService.debug("检测到 CraftEngine 物品，准备检查 CE 食物配置: " + ceConfigId);
            if (foodService.getFoodDefinition(ceConfigId) != null) {
                return ceConfigId;
            }

            foodService.debug("未找到 CE 食物配置，跳过 FoodControl: foods." + ceConfigId);
            return null;
        }

        Material material = item.getType();
        return material.getKey().toString();
    }
}
