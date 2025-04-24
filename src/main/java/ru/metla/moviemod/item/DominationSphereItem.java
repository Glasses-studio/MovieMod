package ru.metla.moviemod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * Сфера господства - декоративный предмет, не имеющий функций.
 * Используется только для демонстрации.
 */
public class DominationSphereItem extends Item {
    
    public DominationSphereItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC) // Эпическая редкость (фиолетовый текст)
                .fireResistant()); // Не сгорает в лаве
    }
} 