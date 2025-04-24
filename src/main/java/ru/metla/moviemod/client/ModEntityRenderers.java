package ru.metla.moviemod.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.metla.moviemod.Moviemod;
import ru.metla.moviemod.entity.ModEntityTypes;

/**
 * Регистрирует рендереры для сущностей на клиентской стороне.
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Moviemod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Регистрируем рендерер для картофельного снаряда
            EntityRenderers.register(ModEntityTypes.THROWN_POTATO.get(), ThrownItemRenderer::new);
        });
    }
} 