package ru.metla.moviemod.effect;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class StunEffect extends MobEffect {
    private static final Vector3f STUN_PARTICLE_COLOR = new Vector3f(255/255f, 243/255f, 113/255f);
    private final Random random = new Random();
    
    public StunEffect() {
        super(MobEffectCategory.HARMFUL, 0xCCCCCC); // Серый цвет для оглушения
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Разное поведение для игроков и мобов
        if (entity instanceof Player) {
            // Для игроков: полное отключение движения
            applyPlayerStun((Player) entity);
        } else if (entity instanceof Mob) {
            // Для мобов: сильное замедление вместо отключения ИИ
            applyMobStun((Mob) entity);
        }
        
        // Особое поведение для криперов (предотвращение взрыва)
        if (entity instanceof Creeper creeper) {
            if (creeper.getSwellDir() > 0) {
                creeper.setSwellDir(-1);
            }
        }
        
        // Добавляем пылевые частицы вокруг верхней части хитбокса для всех
        spawnStunParticles(entity);
    }
    
    private void applyPlayerStun(Player player) {
        // Сохраняем текущее положение игрока
        Vec3 pos = player.position();
        
        // Полностью останавливаем движение (включая вертикальное)
        player.setDeltaMovement(0, 0, 0);
        player.setPos(pos.x, pos.y, pos.z);
        
        // Отменяем физику
        player.hurtMarked = false;
        
        // Добавляем слепоту
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10, 0, false, false, true));
    }
    
    private void applyMobStun(Mob mob) {
        // Вместо отключения ИИ добавляем сильное замедление (255 уровня)
        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 255, false, false, true));
        
        // Отменяем текущий путь
        if (mob.getNavigation() != null) {
            mob.getNavigation().stop();
        }
        
        // Останавливаем атаку, если моб атакует
        mob.setTarget(null);
        
        // Останавливаем движение, оставляя возможность падать
        mob.setDeltaMovement(0, mob.getDeltaMovement().y(), 0);
    }
    
    private void spawnStunParticles(LivingEntity entity) {
        Level level = entity.level();
        if (level.isClientSide()) { // Спавним частицы только на клиентской стороне
            double entityHeight = entity.getBbHeight();
            double particleY = entity.getY() + entityHeight * 0.8; // Спавним в верхней части хитбокса
            
            // Получаем радиус кружения (примерно равен ширине существа)
            double radius = Math.max(0.5, entity.getBbWidth() * 0.7);
            
            // Спавним 5 частиц за тик в случайных местах вокруг головы
            for (int i = 0; i < 5; i++) {
                // Вычисляем случайный угол для спавна частиц по кругу
                double angle = random.nextDouble() * Math.PI * 2;
                
                // Вычисляем позицию частицы
                double particleX = entity.getX() + Math.sin(angle) * radius;
                double particleZ = entity.getZ() + Math.cos(angle) * radius;
                
                // Добавляем небольшие случайные отклонения для красоты
                particleX += (random.nextDouble() - 0.5) * 0.15;
                particleY += (random.nextDouble() - 0.5) * 0.15;
                particleZ += (random.nextDouble() - 0.5) * 0.15;
                
                // Создаем пылевую частицу желтого цвета с увеличенным размером
                DustParticleOptions particleData = new DustParticleOptions(STUN_PARTICLE_COLOR, 1.2f);
                
                // Спавним частицу с нулевой скоростью (она будет висеть в воздухе)
                level.addParticle(particleData, particleX, particleY, particleZ, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Применяем эффект каждый тик
        return true;
    }
    
    @Override
    public List<ItemStack> getCurativeItems() {
        // Возвращаем пустой список, что означает, что никакие предметы не могут вылечить этот эффект,
        // включая молоко, которое по умолчанию лечит все эффекты
        return List.of();
    }
} 