package ru.metla.moviemod.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.metla.moviemod.Moviemod;
import ru.metla.moviemod.effect.ModEffects;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Moviemod.MODID)
public class MobEvents {

    // Генератор случайных чисел для эффектов частиц
    private static final Random random = new Random();
    
    // Идентификатор измерения Незера
    private static final ResourceLocation NETHER_DIMENSION_ID = new ResourceLocation("minecraft", "the_nether");
    
    // Продолжительность эффекта на зомбифицированных существах (5 минут = 6000 тиков)
    private static final int ZOMBIFIED_EFFECT_DURATION = 6000;

    /**
     * Обработчик события добавления эффекта.
     * Если игроку пытаются добавить эффект защиты от зомбификации, отменяем это.
     * Если эффект добавляется зомбифицированному существу, продлеваем его до 5 минут.
     */
    @SubscribeEvent
    public static void onEffectAdd(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();
        
        // Проверяем, является ли сущность игроком и эффект - защитой от зомбификации
        if (entity instanceof Player && effectInstance.getEffect() == ModEffects.ZOMBIFICATION_PROTECTION.get()) {
            // Немедленно удаляем эффект у игрока
            entity.removeEffect(ModEffects.ZOMBIFICATION_PROTECTION.get());
            
            // Отправляем сообщение игроку о том, что эффект не работает на нем
            if (entity.level().isClientSide) {
                ((Player) entity).displayClientMessage(
                    Component.translatable("message.moviemod.immunity_player_failed"), 
                    true
                );
            }
        }
        
        // Проверяем, является ли сущность зомбифицированной и эффект - защитой от зомбификации
        if (effectInstance.getEffect() == ModEffects.ZOMBIFICATION_PROTECTION.get() &&
            (entity instanceof ZombieVillager || entity instanceof ZombifiedPiglin || entity instanceof Zoglin)) {
            
            // Удаляем текущий эффект и добавляем новый с продленным временем
            entity.removeEffect(ModEffects.ZOMBIFICATION_PROTECTION.get());
            entity.addEffect(new MobEffectInstance(
                ModEffects.ZOMBIFICATION_PROTECTION.get(),
                ZOMBIFIED_EFFECT_DURATION, // 5 минут
                effectInstance.getAmplifier() // Сохраняем уровень эффекта
            ));
        }
    }

    /**
     * Обработчик события начала зомбификации существ.
     * Если у существа есть эффект защиты от зомбификации, отменяем процесс.
     */
    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        
        // Проверяем, есть ли у сущности эффект защиты от зомбификации
        if (entity.hasEffect(ModEffects.ZOMBIFICATION_PROTECTION.get())) {
            // Если это пиглин, хоглин или житель, отменяем зомбификацию
            if (entity instanceof Piglin || entity instanceof Hoglin || entity instanceof Villager) {
                // Отменяем зомбификацию
                event.setCanceled(true);
            }
        }
    }
    
    /**
     * Обработчик события обновления живых существ.
     * Используется для отображения частиц защиты у существ с эффектом защиты от зомбификации.
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        
        // Проверяем только раз в 20 тиков (примерно 1 раз в секунду)
        if (entity.level().getGameTime() % 20 != 0) {
            return;
        }
        
        // Проверяем наличие эффекта защиты от зомбификации
        if (entity.hasEffect(ModEffects.ZOMBIFICATION_PROTECTION.get())) {
            // Для зомбифицированных существ также показываем частицы
            if (entity instanceof ZombieVillager || entity instanceof ZombifiedPiglin || entity instanceof Zoglin) {
                showProtectionParticles(entity);
            }
            // Для пиглинов и хоглинов показываем частицы только в Незере
            else if ((entity instanceof Piglin || entity instanceof Hoglin) && 
                    entity.level().dimension().location().equals(NETHER_DIMENSION_ID)) {
                showProtectionParticles(entity);
            }
            // Для жителей показываем частицы в любом измерении
            else if (entity instanceof Villager) {
                showProtectionParticles(entity);
            }
        }
    }
    
    /**
     * Показывает защитные частицы вокруг существа.
     */
    private static void showProtectionParticles(LivingEntity entity) {
        Level level = entity.level();
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() / 2.0;
        double z = entity.getZ();
        
        // Создаем спираль из частиц вокруг существа
        for (int i = 0; i < 5; i++) {
            double offsetX = random.nextDouble() * 0.5 - 0.25;
            double offsetY = random.nextDouble() * 0.5;
            double offsetZ = random.nextDouble() * 0.5 - 0.25;
            
            level.addParticle(
                ParticleTypes.CRIMSON_SPORE, // Красные частицы из багрового леса
                x + offsetX,
                y + offsetY,
                z + offsetZ,
                0, 0, 0
            );
        }
    }
} 