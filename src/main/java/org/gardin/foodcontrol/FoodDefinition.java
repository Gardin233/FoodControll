package org.gardin.foodcontrol;

import org.bukkit.potion.PotionEffect;

import java.util.List;

public record FoodDefinition(
        int nutrition,
        float saturation,
        List<PotionEffect> effects,
        List<String> commands
) {
}
