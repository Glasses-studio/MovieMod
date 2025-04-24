package ru.metla.moviemod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.metla.moviemod.Moviemod;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Moviemod.MODID);
    
    public static final RegistryObject<EntityType<ThrownPotatoProjectile>> THROWN_POTATO = 
            ENTITY_TYPES.register("thrown_potato",
                    () -> EntityType.Builder.<ThrownPotatoProjectile>of((type, world) -> new ThrownPotatoProjectile(type, world), MobCategory.MISC)
                            .sized(0.25F, 0.25F) // Размер снаряда
                            .clientTrackingRange(4) // Дальность отслеживания клиентом
                            .updateInterval(10) // Интервал обновления
                            .build("thrown_potato"));
    
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
} 