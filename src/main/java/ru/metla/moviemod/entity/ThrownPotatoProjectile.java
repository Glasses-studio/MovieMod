package ru.metla.moviemod.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotatoProjectile extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Integer> POTATO_TYPE = SynchedEntityData.defineId(ThrownPotatoProjectile.class, EntityDataSerializers.INT);
    
    private PotatoType potatoType = PotatoType.NORMAL; // По умолчанию обычный картофель

    public ThrownPotatoProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownPotatoProjectile(Level level, LivingEntity shooter) {
        // Тип должен быть определен после регистрации
        super(ModEntityTypes.THROWN_POTATO.get(), shooter, level);
    }

    public ThrownPotatoProjectile(Level level, double x, double y, double z) {
        // Тип должен быть определен после регистрации
        super(ModEntityTypes.THROWN_POTATO.get(), x, y, z, level);
    }

    public void setPotatoType(PotatoType type) {
        this.potatoType = type;
    }

    @Override
    protected Item getDefaultItem() {
        return potatoType.getItem();
    }

    /**
     * Возвращает ItemStack, представляющий визуальный вид снаряда
     */
    @Override
    public ItemStack getItem() {
        return new ItemStack(potatoType.getItem());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("PotatoType", potatoType.ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("PotatoType")) {
            int typeOrdinal = tag.getInt("PotatoType");
            if (typeOrdinal >= 0 && typeOrdinal < PotatoType.values().length) {
                this.potatoType = PotatoType.values()[typeOrdinal];
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            // Визуальный эффект в зависимости от типа картофеля
            ParticleOptions particleOptions = potatoType.getParticle();
            this.level().broadcastEntityEvent(this, (byte)3);
            
            // Создаем частицы на сервере, которые будут отправлены клиентам
            if (particleOptions != null) {
                for (int i = 0; i < 8; i++) {
                    double offsetX = this.random.nextGaussian() * 0.02D;
                    double offsetY = this.random.nextGaussian() * 0.02D;
                    double offsetZ = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(particleOptions, 
                            this.getX(), this.getY(), this.getZ(), 
                            offsetX, offsetY + 0.1D, offsetZ);
                }
            }
            
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            // Применяем урон и эффекты в зависимости от типа картофеля
            livingEntity.hurt(this.damageSources().thrown(this, this.getOwner()), potatoType.getDamage());
            
            // Применяем дополнительные эффекты
            potatoType.applyEffects(livingEntity);
        }
    }
    
    /**
     * Типы картофеля с разными эффектами
     */
    public enum PotatoType {
        NORMAL(Items.POTATO, 5.0F, ParticleTypes.CRIT),
        BAKED(Items.BAKED_POTATO, 4.0F, ParticleTypes.FLAME),
        POISONOUS(Items.POISONOUS_POTATO, 3.0F, ParticleTypes.ENTITY_EFFECT);
        
        private final Item item;
        private final float damage;
        private final ParticleOptions particle;
        
        PotatoType(Item item, float damage, ParticleOptions particle) {
            this.item = item;
            this.damage = damage;
            this.particle = particle;
        }
        
        public Item getItem() {
            return item;
        }
        
        public float getDamage() {
            return damage;
        }
        
        public ParticleOptions getParticle() {
            return particle;
        }
        
        public void applyEffects(LivingEntity target) {
            switch (this) {
                case BAKED:
                    // Поджигаем цель на 3 секунды
                    target.setSecondsOnFire(3);
                    break;
                case POISONOUS:
                    // Отравляем цель на 5 секунд (уровень 0, 100 тиков = 5 секунд)
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    break;
                default:
                    // Обычный картофель не имеет дополнительных эффектов
                    break;
            }
        }
        
        public static PotatoType fromItem(Item item) {
            if (item == Items.POISONOUS_POTATO) {
                return POISONOUS;
            } else if (item == Items.BAKED_POTATO) {
                return BAKED;
            } else {
                return NORMAL;
            }
        }
    }
} 