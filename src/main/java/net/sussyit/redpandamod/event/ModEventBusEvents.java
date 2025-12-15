package net.sussyit.redpandamod.event;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.ModEntities;
import net.sussyit.redpandamod.entity.client.PiglinBossModel;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

@EventBusSubscriber(modid = RedPandaMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PiglinBossModel.LAYER_LOCATION, PiglinBossModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.PIGLINBOSS.get(), PiglinBossEntity.createLivingAttributes().build());
    }
}
