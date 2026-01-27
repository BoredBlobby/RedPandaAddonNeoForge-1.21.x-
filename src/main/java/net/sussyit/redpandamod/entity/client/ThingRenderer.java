package net.sussyit.redpandamod.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.ThingEntity;

public class ThingRenderer extends LivingEntityRenderer<ThingEntity, ThingModel<ThingEntity>> {

    private static final ResourceLocation NORMAL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/thing/thing.png");

    private static final ResourceLocation TRANSITION_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/thing/thing2.png");

    private static final ResourceLocation SMILING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/thing/thing3.png");


    public ThingRenderer(EntityRendererProvider.Context context) {
        super(context, new ThingModel<>(context.bakeLayer(ThingModel.LAYER_LOCATION)), 2);
    }

    @Override
    public ResourceLocation getTextureLocation(ThingEntity thingEntity) {
        return NORMAL_TEXTURE;
    }
}
