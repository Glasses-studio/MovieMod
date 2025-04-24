package ru.metla.moviemod.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LavaChickenItem extends Item {
    public LavaChickenItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack resultStack = super.finishUsingItem(stack, level, entity);
        
        if (level.isClientSide && entity instanceof Player) {
            double x = entity.getX();
            double y = entity.getY() + 1.0D;
            double z = entity.getZ();
            
            for (int i = 0; i < 8; i++) {
                double offsetX = level.random.nextDouble() * 0.6D - 0.3D;
                double offsetY = level.random.nextDouble() * 0.6D - 0.3D;
                double offsetZ = level.random.nextDouble() * 0.6D - 0.3D;
                
                level.addParticle(ParticleTypes.LAVA,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0.0D, 0.0D, 0.0D);
            }
        }
        
        return resultStack;
    }
} 