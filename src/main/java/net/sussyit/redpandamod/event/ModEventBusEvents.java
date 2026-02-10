package net.sussyit.redpandamod.event;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.ModEntities;
import net.sussyit.redpandamod.entity.client.GfModel;
import net.sussyit.redpandamod.entity.client.HedgehogModel;
import net.sussyit.redpandamod.entity.client.PiglinBossModel;
import net.sussyit.redpandamod.entity.client.ThingModel;
import net.sussyit.redpandamod.entity.custom.GfEntity;
import net.sussyit.redpandamod.entity.custom.HedgehogEntity;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.entity.custom.ThingEntity;

@EventBusSubscriber(modid = RedPandaMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PiglinBossModel.LAYER_LOCATION, PiglinBossModel::createBodyLayer);
        event.registerLayerDefinition(HedgehogModel.LAYER_LOCATION, HedgehogModel::createBodyLayer);
        event.registerLayerDefinition(ThingModel.LAYER_LOCATION, ThingModel::createBodyLayer);
        event.registerLayerDefinition(GfModel.LAYER_LOCATION, GfModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.PIGLINBOSS.get(), PiglinBossEntity.createAttributes().build());
        event.put(ModEntities.HEDGEHOG.get(), HedgehogEntity.createAttributes().build());
        event.put(ModEntities.THING.get(), ThingEntity.createAttributes().build());
        event.put(ModEntities.GF.get(), GfEntity.createAttributes().build());
    }
}
