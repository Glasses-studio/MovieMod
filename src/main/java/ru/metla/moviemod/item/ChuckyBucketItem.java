package ru.metla.moviemod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.network.chat.Component;
import ru.metla.moviemod.effect.ModEffects;

public class ChuckyBucketItem extends SwordItem {

    public ChuckyBucketItem() {
        // У железного меча урон 6, делаем на 1 меньше = 5
        super(Tiers.IRON, 5, -2.0F, 
                new Item.Properties()
                        .stacksTo(1)
                        .durability(500) // Прочность 500
        );
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        
        // 80% шанс добавления эффекта оглушения на 8 секунд (160 тиков)
        if (target.level().getRandom().nextFloat() < 0.8f) {
            target.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 160, 0));
            
            // Если цель - игрок, показываем сообщение о том, что он ослеплен
            if (target instanceof Player player) {
                player.displayClientMessage(Component.translatable("message.moviemod.stun_blind"), true);
            }
        }
        
        return result;
    }
} 