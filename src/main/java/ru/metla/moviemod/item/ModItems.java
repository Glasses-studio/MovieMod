package ru.metla.moviemod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.metla.moviemod.Moviemod;

public class ModItems {
    private static final Item.Properties DEFAULT_PROPERTIES = new Item.Properties();
    
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Moviemod.MODID);

    public static final RegistryObject<Item> TESTI = ITEMS.register("testi",
            () -> new Item(DEFAULT_PROPERTIES));
    public static final RegistryObject<Item> LAVACHICKEN = ITEMS.register("lavachicken",
            () -> new Item(new Item.Properties().food(ModFood.LAVACHICKEN)));

    public static final RegistryObject<Item> CHUCKY_BUCKET = ITEMS.register("chucky_bucket",
            ChuckyBucketItem::new);

    public static final RegistryObject<Item> POTATO_CANNON = ITEMS.register("potato_cannon",
            PotatoCannonItem::new);

    // Боевые топоры разных материалов
    public static final RegistryObject<Item> WOODEN_BATTLE_AXE = ITEMS.register("wooden_battle_axe",
            () -> new BattleAxeItem(Tiers.WOOD));
    
    public static final RegistryObject<Item> STONE_BATTLE_AXE = ITEMS.register("stone_battle_axe",
            () -> new BattleAxeItem(Tiers.STONE));
    
    public static final RegistryObject<Item> IRON_BATTLE_AXE = ITEMS.register("iron_battle_axe",
            () -> new BattleAxeItem(Tiers.IRON));
    
    public static final RegistryObject<Item> GOLD_BATTLE_AXE = ITEMS.register("gold_battle_axe",
            () -> new BattleAxeItem(Tiers.GOLD));
    
    public static final RegistryObject<Item> DIAMOND_BATTLE_AXE = ITEMS.register("diamond_battle_axe",
            () -> new BattleAxeItem(Tiers.DIAMOND));
    
    public static final RegistryObject<Item> NETHERITE_BATTLE_AXE = ITEMS.register("netherite_battle_axe",
            () -> new BattleAxeItem(Tiers.NETHERITE));

    // Для обратной совместимости оставляем старую регистрацию, но указываем железный материал
    @Deprecated
    public static final RegistryObject<Item> BATTLE_AXE = ITEMS.register("battle_axe",
            () -> new BattleAxeItem(Tiers.IRON));

    // Декоративные предметы
    public static final RegistryObject<Item> DOMINATION_SPHERE = ITEMS.register("domination_sphere",
            DominationSphereItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
