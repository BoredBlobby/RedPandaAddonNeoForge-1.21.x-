package net.sussyit.redpandamod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sussyit.redpandamod.RedPandaMod;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RedPandaMod.MODID);

    public static final DeferredItem<Item> RED_PANDA_TREAT = ITEMS.register("redpandatreat",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> GOLD_RING = ITEMS.register("goldring",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CHOCOLATEBOX = ITEMS.register("chocolatebox",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<ScytheItem> SCYTHE = ITEMS.register("scythe",
            () -> new ScytheItem(Tiers.DIAMOND, new Item.Properties()
                    .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4f))));
    public static final DeferredItem<Item> MATCHABOBA = ITEMS.register("matchaboba",
            () -> new MatchaBoba(new Item.Properties().stacksTo(1)));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
