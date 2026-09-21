package org.gardin.foodcontrol.utils;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public final class banCE {

    private static Method isCustomItemMethodRef;
    private static Method customItemIdMethodRef;
    private static Method byItemStackMethodRef;
    private static boolean lookupAttempted;

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

        try {
            Method isCustomMethod = getIsCustomItemMethod();
            if (isCustomMethod != null) {
                Object result = isCustomMethod.invoke(null, item);
                if (result instanceof Boolean bool && bool) {
                    return true;
                }
            }

            Method byItemMethod = getByItemStackMethod();
            if (byItemMethod != null) {
                return byItemMethod.invoke(null, item) != null;
            }
        } catch (ReflectiveOperationException ignored) {
            return false;
        }

        return false;
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

        try {
            Method customItemIdMethod = getCustomItemIdMethod();
            if (customItemIdMethod == null) {
                return "unknown";
            }

            Object key = customItemIdMethod.invoke(null, item);
            return key == null ? "unknown" : String.valueOf(key);
        } catch (ReflectiveOperationException ignored) {
            return "unknown";
        }
    }

    public static String getCraftEngineConfigId(ItemStack item) {
        String ceId = getCraftEngineId(item);
        if (ceId == null || ceId.isBlank() || "unknown".equalsIgnoreCase(ceId)) {
            return null;
        }

        return "CE:" + ceId;
    }

    private static Method getIsCustomItemMethod() {
        loadMethodsIfNeeded();
        return isCustomItemMethodRef;
    }

    private static Method getCustomItemIdMethod() {
        loadMethodsIfNeeded();
        return customItemIdMethodRef;
    }

    private static Method getByItemStackMethod() {
        loadMethodsIfNeeded();
        return byItemStackMethodRef;
    }

    private static void loadMethodsIfNeeded() {
        if (lookupAttempted) {
            return;
        }
        lookupAttempted = true;

        try {
            Class<?> craftEngineItemsClass = Class.forName("net.momirealms.craftengine.bukkit.api.CraftEngineItems");
            isCustomItemMethodRef = craftEngineItemsClass.getMethod("isCustomItem", ItemStack.class);
            customItemIdMethodRef = craftEngineItemsClass.getMethod("getCustomItemId", ItemStack.class);
            byItemStackMethodRef = craftEngineItemsClass.getMethod("byItemStack", ItemStack.class);
        } catch (ReflectiveOperationException ignored) {
            isCustomItemMethodRef = null;
            customItemIdMethodRef = null;
            byItemStackMethodRef = null;
        }
    }
}
