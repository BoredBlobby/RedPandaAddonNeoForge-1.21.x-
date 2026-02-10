package net.sussyit.redpandamod.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Wolf;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.GfEntity;
import net.sussyit.redpandamod.entity.custom.HedgehogEntity;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.entity.custom.ThingEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RedPandaMod.MODID);

    public static final Supplier<EntityType<PiglinBossEntity>> PIGLINBOSS =
            ENTITY_TYPES.register("piglinboss", () -> EntityType.Builder.of(PiglinBossEntity::new, MobCategory.CREATURE)
                    .sized(2f, 1.5f).build("piglingboss"));

    public static final Supplier<EntityType<HedgehogEntity>> HEDGEHOG =
            ENTITY_TYPES.register("hedgehog", () -> EntityType.Builder.of(HedgehogEntity::new, MobCategory.CREATURE)
                    .sized(.3f, .3f).build("hedgehog"));

    public static final Supplier<EntityType<ThingEntity>> THING =
            ENTITY_TYPES.register("thing", () -> EntityType.Builder.of(ThingEntity::new, MobCategory.CREATURE)
                    .sized(2f, 1.5f).build("thing"));

    public static final Supplier<EntityType<GfEntity>> GF =
            ENTITY_TYPES.register("gf", () -> EntityType.Builder.of(GfEntity::new, MobCategory.CREATURE)
                    .sized(2f, 1.5f).build("gf"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
