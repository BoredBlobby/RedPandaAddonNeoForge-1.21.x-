package net.sussyit.redpandamod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RedPandaMod.MODID);

    public static final Supplier<CreativeModeTab> RED_PANDA_ITEMS = CREATIVE_MODE_TAB.register("red_panda_items",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RED_PANDA_TREAT.get()))
                    .title(Component.translatable("creativetab.redpandamod.red_panda_items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.RED_PANDA_TREAT);
                        output.accept(ModBlocks.DANIEL_CHUNG);
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
