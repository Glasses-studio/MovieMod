package ru.metla.moviemod.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.metla.moviemod.Moviemod;

@Mod.EventBusSubscriber(modid = Moviemod.MODID)
public class StunEvents {

    // Отменяем прыжки для оглушенных сущностей
    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            event.getEntity().setDeltaMovement(
                event.getEntity().getDeltaMovement().x(),
                0,
                event.getEntity().getDeltaMovement().z()
            );
        }
    }

    // Отменяем взаимодействия для оглушенных игроков
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getEntity();
        if (player.hasEffect(ModEffects.STUN.get())) {
            event.setCanceled(true);
        }
    }
    
    // Отменяем начало использования предмета для оглушенных игроков
    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            event.setCanceled(true);
        }
    }
    
    // Отменяем завершение использования предмета для оглушенных игроков
    // (На случай, если эффект применился во время использования предмета)
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity().hasEffect(ModEffects.STUN.get())) {
            // Специальная обработка для молока - отменяем его действие
            if (event.getItem().getItem() == Items.MILK_BUCKET) {
                event.setCanceled(true);
            }
        }
    }
    
    // Предотвращаем удаление эффекта оглушения
    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        
        // Проверяем, это ли эффект оглушения
        if (effectInstance != null && effectInstance.getEffect() == ModEffects.STUN.get()) {
            // Проверяем, не пытаются ли удалить эффект с помощью молока или другим способом
            // кроме естественного истечения времени действия
            if (effectInstance.getDuration() > 0) {
                event.setResult(Event.Result.DENY);
            }
        }
    }
} 