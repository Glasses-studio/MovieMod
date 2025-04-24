package ru.metla.moviemod.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import ru.metla.moviemod.entity.ThrownPotatoProjectile;
import ru.metla.moviemod.entity.ThrownPotatoProjectile.PotatoType;

public class PotatoCannonItem extends Item {
    private static final int COOLDOWN_TIME = 30; // 1.5 секунды (20 тиков в секунду)
    
    public PotatoCannonItem() {
        super(new Item.Properties()
                .stacksTo(1)); // Removing durability to make it indestructible
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // Проверяем, не на кулдауне ли предмет
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemstack);
        }
        
        // Проверяем наличие любого типа картофеля
        Item potatoItem = null;
        int potatoSlot = -1;
        
        // Сначала ищем ядовитый картофель (приоритет на более редкие)
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == Items.POISONOUS_POTATO) {
                potatoItem = Items.POISONOUS_POTATO;
                potatoSlot = i;
                break;
            }
        }
        
        // Если не нашли ядовитый, ищем жареный картофель
        if (potatoItem == null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == Items.BAKED_POTATO) {
                    potatoItem = Items.BAKED_POTATO;
                    potatoSlot = i;
                    break;
                }
            }
        }
        
        // Если не нашли жареный, ищем обычный картофель
        if (potatoItem == null) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == Items.POTATO) {
                    potatoItem = Items.POTATO;
                    potatoSlot = i;
                    break;
                }
            }
        }
        
        // Если не нашли никакой картофель и игрок не в креативе, то ошибка
        if (potatoItem == null && !player.getAbilities().instabuild) {
            return InteractionResultHolder.fail(itemstack);
        }
        
        // Определяем тип картофеля
        PotatoType potatoType = PotatoType.NORMAL; // По умолчанию
        if (potatoItem != null) {
            potatoType = PotatoType.fromItem(potatoItem);
        }
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.DISPENSER_LAUNCH, SoundSource.PLAYERS, 
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F));
        
        if (!level.isClientSide) {
            // Создаем и запускаем картофель как снаряд с правильным типом
            ThrownPotatoProjectile potato = new ThrownPotatoProjectile(level, player);
            potato.setPotatoType(potatoType);
            potato.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(potato);
            
            // Расходуем один картофель из инвентаря только если игрок не в креативе
            if (!player.getAbilities().instabuild && potatoSlot >= 0) {
                ItemStack stack = player.getInventory().getItem(potatoSlot);
                stack.shrink(1);
                if (stack.isEmpty()) {
                    player.getInventory().setItem(potatoSlot, ItemStack.EMPTY);
                }
            }
        }
        
        // Устанавливаем кулдаун в 1.5 секунды (30 тиков)
        player.getCooldowns().addCooldown(this, COOLDOWN_TIME);
        
        // Засчитываем использование предмета для статистики
        player.awardStat(Stats.ITEM_USED.get(this));
        
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
} 