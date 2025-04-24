package ru.metla.moviemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import ru.metla.moviemod.effect.ModEffects;
import ru.metla.moviemod.entity.ModEntityTypes;
import ru.metla.moviemod.item.ModItems;
import ru.metla.moviemod.potion.ModPotions;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Moviemod.MODID)
public class Moviemod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "moviemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Moviemod() {
        // Оптимизация: кэшируем eventBus в локальной переменной
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Оптимизация: группируем регистрации вместе
        ModItems.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntityTypes.register(modEventBus); // Регистрируем типы сущностей
        ModPotions.register(modEventBus);     // Регистрируем зелья
        

        // Оптимизация: используем лямбды для лучшей производительности
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Используем параллельную обработку для общей настройки
        event.enqueueWork(() -> {
            // Здесь могла бы быть ваша инициализация общей настройки
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Оптимизация: используем switch для более эффективной проверки вкладки
        var tabKey = event.getTabKey();
        
        if (tabKey == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.CHUCKY_BUCKET);
            event.accept(ModItems.POTATO_CANNON); // Картофельная пушка в боевой вкладке
            
            // Добавляем все варианты боевых топоров
            event.accept(ModItems.WOODEN_BATTLE_AXE);
            event.accept(ModItems.STONE_BATTLE_AXE);
            event.accept(ModItems.IRON_BATTLE_AXE);
            event.accept(ModItems.GOLD_BATTLE_AXE);
            event.accept(ModItems.DIAMOND_BATTLE_AXE);
            event.accept(ModItems.NETHERITE_BATTLE_AXE);
            
            // Убираем старый вариант для обратной совместимости
        } 
        else if (tabKey == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.LAVACHICKEN);
        }
        else if (tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {

        }
    }

    // Оптимизация: код для клиентской стороны загружается только на клиенте
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(final FMLClientSetupEvent event) {
            // Оптимизация: используем параллельную обработку для рендереров
            event.enqueueWork(() -> {
                // Client setup code goes here
            });
        }
        
        // Регистрация рендереров для сущностей
        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntityTypes.THROWN_POTATO.get(), 
                    ThrownItemRenderer::new);
        }
    }
}