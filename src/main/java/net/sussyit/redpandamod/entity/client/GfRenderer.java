package net.sussyit.redpandamod.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.GfEntity;

public class GfRenderer extends MobRenderer<GfEntity, GfModel<GfEntity>> {

    private static final ResourceLocation GF1_PHASE1 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf1.png");

    public GfRenderer(EntityRendererProvider.Context context) {
        super(context, new GfModel<>(context.bakeLayer(GfModel.LAYER_LOCATION)), 1);
    }

    @Override
    public ResourceLocation getTextureLocation(GfEntity gfEntity) {
        return GF1_PHASE1;
    }
}
