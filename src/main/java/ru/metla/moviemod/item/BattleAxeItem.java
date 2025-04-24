package ru.metla.moviemod.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Боевой топор различных материалов.
 * Обладает особой способностью при нажатии правой кнопки мыши,
 * сила которой зависит от материала.
 */
public class BattleAxeItem extends AxeItem {
    private static final int ABILITY_COOLDOWN = 70; // 3.5 секунды (20 тиков в секунду)
    
    // Различные свойства в зависимости от материала
    private final float abilityDamage;
    private final float abilityRadius;
    private final float abilityKnockupStrength;
    
    /**
     * Создает боевой топор из указанного материала
     * 
     * @param tier Материал топора
     */
    public BattleAxeItem(Tier tier) {
        // Используем стандартные значения урона и скорости для топоров данного материала,
        // но немного увеличиваем базовый урон
        super(tier, tier.getAttackDamageBonus() + 1.0F, -3.1F, 
              new Item.Properties().stacksTo(1));
              
        // Настраиваем характеристики способности в зависимости от уровня материала
        switch (tier.getLevel()) {
            case 0: // Дерево
                this.abilityDamage = 4.0F;
                this.abilityRadius = 2.0F;
                this.abilityKnockupStrength = 0.8F;
                break;
            case 1: // Камень
                this.abilityDamage = 5.0F;
                this.abilityRadius = 2.5F;
                this.abilityKnockupStrength = 1.0F;
                break;
            case 2: // Железо
                this.abilityDamage = 7.0F;
                this.abilityRadius = 3.0F;
                this.abilityKnockupStrength = 1.5F;
                break;
            case 3: // Алмаз
                this.abilityDamage = 9.0F;
                this.abilityRadius = 3.5F;
                this.abilityKnockupStrength = 1.8F;
                break;
            case 4: // Незерит
                this.abilityDamage = 12.0F;
                this.abilityRadius = 4.0F;
                this.abilityKnockupStrength = 2.2F;
                break;
            default: // По умолчанию как железо
                this.abilityDamage = 7.0F;
                this.abilityRadius = 3.0F;
                this.abilityKnockupStrength = 1.5F;
                break;
        }
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Проверяем, не на кулдауне ли предмет
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemstack);
        }
        
        // Активируем способность
        if (!level.isClientSide) {
            // Создаем эффект частиц и звук
            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 lookVector = player.getLookAngle();
            
            // Позиция перед игроком
            Vec3 abilityPos = player.getEyePosition()
                    .add(lookVector.x * 2.0, 0, lookVector.z * 2.0);
            
            // Создаем частицы в кругу перед игроком
            createParticleCircle(serverLevel, abilityPos, this.abilityRadius);
            
            // Воспроизводим звук
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 0.5F);
            
            // Находим всех существ в радиусе действия способности
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(abilityPos.x - this.abilityRadius, abilityPos.y - 1, abilityPos.z - this.abilityRadius,
                            abilityPos.x + this.abilityRadius, abilityPos.y + 2, abilityPos.z + this.abilityRadius));
            
            // Применяем эффекты ко всем найденным существам (кроме самого игрока)
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Наносим урон
                    entity.hurt(entity.damageSources().playerAttack(player), this.abilityDamage);
                    
                    // Подбрасываем вверх
                    entity.setDeltaMovement(entity.getDeltaMovement().x, this.abilityKnockupStrength, entity.getDeltaMovement().z);
                }
            }
        }
        
        // Устанавливаем кулдаун
        player.getCooldowns().addCooldown(this, ABILITY_COOLDOWN);
        
        // Засчитываем использование предмета для статистики
        player.awardStat(Stats.ITEM_USED.get(this));
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
    
    /**
     * Создает круг из частиц вокруг заданной позиции
     */
    private void createParticleCircle(ServerLevel level, Vec3 center, float radius) {
        int particleCount = 100; // Количество частиц
        
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            
            // Создаем белые частицы (снежинки)
            level.sendParticles(
                    ParticleTypes.SNOWFLAKE,
                    x, center.y, z,
                    3, // количество частиц в одной точке
                    0.1, 0.1, 0.1, // разброс частиц
                    0.05 // скорость частиц
            );
            
            // Добавляем частицы вдоль радиуса для заполнения круга
            if (i % 5 == 0) {
                for (double r = 0.5; r < radius; r += 0.5) {
                    double xr = center.x + r * Math.cos(angle);
                    double zr = center.z + r * Math.sin(angle);
                    level.sendParticles(
                            ParticleTypes.SNOWFLAKE,
                            xr, center.y, zr,
                            1,
                            0.1, 0.1, 0.1,
                            0.01
                    );
                }
            }
        }
    }
} 