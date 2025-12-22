package net.sussyit.redpandamod.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RedPandaMod.MODID);

    public static final Supplier<EntityType<PiglinBossEntity>> PIGLINBOSS =
            ENTITY_TYPES.register("piglinboss", () -> EntityType.Builder.of(PiglinBossEntity::new, MobCategory.CREATURE)
                    .sized(2f, 1.5f).build("piglingboss"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
