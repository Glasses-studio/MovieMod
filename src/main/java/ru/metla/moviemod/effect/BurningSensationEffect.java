package ru.metla.moviemod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;

public class BurningSensationEffect extends MobEffect {
    public BurningSensationEffect() {
        super(MobEffectCategory.HARMFUL, 0xFF4500); // Orange-red color
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        
        if (level.isClientSide) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();

            for (int i = 0; i < 2 + amplifier; i++) {
                double offsetX = level.random.nextDouble() * 0.8D - 0.4D;
                double offsetY = level.random.nextDouble() * 1.6D;
                double offsetZ = level.random.nextDouble() * 0.8D - 0.4D;

                level.addParticle(ParticleTypes.FLAME,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0.0D, 0.05D, 0.0D);
            }
        }

        if (!level.isClientSide) {
            // Уменьшаем урон и делаем его чаще
            if (level.getGameTime() % 10 == 0) { // Каждые полсекунды
                float damage = 0.25F + (amplifier * 0.15F); // Базовый урон 0.25 (пол-сердца) + небольшой бонус от уровня
                entity.hurt(entity.damageSources().onFire(), damage);
            }

            // Создаем огонь под игроком
            BlockPos pos = entity.blockPosition();
            if (level.getBlockState(pos).isAir()) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        }

        // Добавляем ускорение движения
        if (entity instanceof Player player) {
            // Получаем направление взгляда игрока
            Vec3 lookAngle = player.getLookAngle();
            
            // Устанавливаем скорость движения (как Speed II)
            double speedBoost = 0.07D * (1 + amplifier);
            
            // Применяем ускорение в направлении взгляда
            player.setDeltaMovement(
                player.getDeltaMovement().add(
                    lookAngle.x * speedBoost,
                    0.0D, // Не добавляем вертикальное ускорение
                    lookAngle.z * speedBoost
                )
            );
            
            // Ограничиваем максимальную скорость
            Vec3 motion = player.getDeltaMovement();
            double maxSpeed = 0.6D + (0.2D * amplifier);
            double currentSpeed = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            
            if (currentSpeed > maxSpeed) {
                double scale = maxSpeed / currentSpeed;
                player.setDeltaMovement(
                    motion.x * scale,
                    motion.y,
                    motion.z * scale
                );
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Effect ticks every tick
    }
} 