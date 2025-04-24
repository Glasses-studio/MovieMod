package ru.metla.moviemod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.metla.moviemod.Moviemod;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = 
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Moviemod.MODID);

    public static final RegistryObject<MobEffect> BURNING_SENSATION = 
            MOB_EFFECTS.register("burning_sensation", BurningSensationEffect::new);
            
    public static final RegistryObject<MobEffect> STUN = 
            MOB_EFFECTS.register("stun", StunEffect::new);
            
    public static final RegistryObject<MobEffect> ZOMBIFICATION_PROTECTION = 
            MOB_EFFECTS.register("zombification_protection", ZombificationProtectionEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
} 