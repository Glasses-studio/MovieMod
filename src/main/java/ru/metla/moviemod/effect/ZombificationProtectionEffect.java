package ru.metla.moviemod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Эффект, который защищает существ от зомбификации:
 * - Пиглинов и хоглинов от превращения в зомбифицированные версии в Незере
 * - Жителей деревень от превращения в зомби
 */
public class ZombificationProtectionEffect extends MobEffect {
    
    public ZombificationProtectionEffect() {
        // Категория BENEFICIAL - для положительных эффектов
        // 0xE42D2D - красный цвет для эффекта (цвет частиц)
        super(MobEffectCategory.BENEFICIAL, 0xE42D2D);
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Эффект не применяется каждый тик, он просто существует
        return false;
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Этот эффект пассивный, он ничего не делает при тике
        // Основная логика в классе MobEvents
    }
} 