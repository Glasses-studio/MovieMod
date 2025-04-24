package ru.metla.moviemod.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.metla.moviemod.Moviemod;

/**
 * Обработчик событий для боевого топора.
 * Основная способность (атака по правой кнопке) реализована в классе BattleAxeItem.
 * Здесь могут быть реализованы дополнительные пассивные эффекты.
 */
@Mod.EventBusSubscriber(modid = Moviemod.MODID)
public class BattleAxeEvents {
    
    /**
     * Обрабатывает события нанесения урона.
     * Здесь можно реализовать дополнительные пассивные эффекты топора.
     */
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // Проверяем, является ли атакующий игроком
        if (event.getSource().getEntity() instanceof Player player) {
            // Получаем предмет в руке игрока
            ItemStack heldItem = player.getMainHandItem();
            
            // Проверяем, является ли предмет боевым топором
            if (heldItem.getItem() instanceof BattleAxeItem) {
                // Примеры дополнительных пассивных эффектов:
                // - Увеличение урона против определенных типов существ
                // - Дополнительные эффекты при критическом ударе
                // - Вампиризм (восстановление здоровья при ударе)
                
                // В настоящее время пассивных эффектов нет,
                // основная способность реализована через правый клик
            }
        }
    }
} 