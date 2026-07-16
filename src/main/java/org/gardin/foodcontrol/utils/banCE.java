package org.gardin.foodcontrol.utils;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import org.bukkit.inventory.ItemStack;

public final class banCE {

    private banCE() {
    }


    /**
     * 判断是否为 CraftEngine 自定义物品
     *
     * @param item 物品
     * @return true = CraftEngine物品
     */
    public static boolean isCraftEngineItem(ItemStack item) {

        if (item == null) {
            return false;
        }

        return CraftEngineItems.isCustomItem(item);
    }


    /**
     * 获取 CraftEngine 自定义物品ID
     *
     * @param item 物品
     * @return CE ID，不是CE物品返回null
     */
    public static String getCraftEngineId(ItemStack item) {

        if (!isCraftEngineItem(item)) {
            return null;
        }

        return String.valueOf(
                CraftEngineItems.getCustomItemId(item)
        );
    }
}