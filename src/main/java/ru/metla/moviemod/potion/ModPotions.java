package ru.metla.moviemod.potion;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.metla.moviemod.Moviemod;
import ru.metla.moviemod.effect.ModEffects;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = 
        DeferredRegister.create(ForgeRegistries.POTIONS, Moviemod.MODID);
        
    // Регистрируем зелье защиты от зомбификации
    // Обычное - 2 минуты (2400 тиков)
    public static final RegistryObject<Potion> ZOMBIFICATION_PROTECTION = 
        POTIONS.register("zombification_protection", 
            () -> new Potion(
                new MobEffectInstance(
                    ModEffects.ZOMBIFICATION_PROTECTION.get(), 
                    2400, // 2 минуты
                    0     // уровень 0 (I)
                )
            )
        );
        
    // Увеличенное - 8 минут (9600 тиков)
    public static final RegistryObject<Potion> LONG_ZOMBIFICATION_PROTECTION = 
        POTIONS.register("long_zombification_protection", 
            () -> new Potion(
                new MobEffectInstance(
                    ModEffects.ZOMBIFICATION_PROTECTION.get(), 
                    9600, // 8 минут
                    0     // уровень 0 (I)
                )
            )
        );
        
    // Усиленное - 1.5 минуты (1800 тиков), но с уровнем II
    public static final RegistryObject<Potion> STRONG_ZOMBIFICATION_PROTECTION = 
        POTIONS.register("strong_zombification_protection", 
            () -> new Potion(
                new MobEffectInstance(
                    ModEffects.ZOMBIFICATION_PROTECTION.get(), 
                    1800, // 1.5 минуты
                    1     // уровень 1 (II)
                )
            )
        );
        
    /**
     * Регистрирует все рецепты зелий
     */
    public static void registerBrewingRecipes() {
        // Базовое зелье - зелье слабости + золотое яблоко
        BrewingRecipeRegistry.addRecipe(
            Ingredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), Potions.WEAKNESS)), 
            Ingredient.of(Items.GOLDEN_APPLE),
            PotionUtils.setPotion(Items.POTION.getDefaultInstance(), ZOMBIFICATION_PROTECTION.get())
        );
        
        // Увеличенное зелье - обычное + красная пыль
        BrewingRecipeRegistry.addRecipe(
            Ingredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), ZOMBIFICATION_PROTECTION.get())),
            Ingredient.of(Items.REDSTONE),
            PotionUtils.setPotion(Items.POTION.getDefaultInstance(), LONG_ZOMBIFICATION_PROTECTION.get())
        );
        
        // Усиленное зелье - обычное + светопыль
        BrewingRecipeRegistry.addRecipe(
            Ingredient.of(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), ZOMBIFICATION_PROTECTION.get())),
            Ingredient.of(Items.GLOWSTONE_DUST),
            PotionUtils.setPotion(Items.POTION.getDefaultInstance(), STRONG_ZOMBIFICATION_PROTECTION.get())
        );
    }
    
    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
        
        // Добавляем обработчик события FMLCommonSetupEvent для регистрации рецептов
        eventBus.addListener((FMLCommonSetupEvent event) -> {
            event.enqueueWork(ModPotions::registerBrewingRecipes);
        });
    }
} 