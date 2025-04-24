package ru.metla.moviemod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import ru.metla.moviemod.effect.ModEffects;

public class ModFood {
    public static final FoodProperties LAVACHICKEN = new FoodProperties.Builder()
            .nutrition(20)
            .saturationMod(1.8f)
            .meat()
            .alwaysEat()
            .effect(() -> new MobEffectInstance(ModEffects.BURNING_SENSATION.get(), 180, 0), 1f) 
            .build();
}