package ru.metla.moviemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Moviemod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", false);

    private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, 100);

    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // Оптимизация: используем простой список из одного элемента
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    // Оптимизация: упрощенная проверка валидности объекта
    private static boolean validateItemName(final Object obj) {
        if (obj instanceof String itemName) {
            return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
        }
        return false;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // Оптимизация: проверяем, относится ли событие к нашей конфигурации
        if (event.getConfig().getSpec() == SPEC) {
            logDirtBlock = LOG_DIRT_BLOCK.get();
            magicNumber = MAGIC_NUMBER.get();
            magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

            // Оптимизация: используем более быстрый способ преобразования списка, пре-аллоцируя размер коллекции
            List<? extends String> itemStringList = ITEM_STRINGS.get();
            items = new HashSet<>(itemStringList.size());
            for (String itemName : itemStringList) {
                items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)));
            }
        }
    }
}
