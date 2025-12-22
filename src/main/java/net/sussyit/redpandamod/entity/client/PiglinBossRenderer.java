package net.sussyit.redpandamod.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

public class PiglinBossRenderer extends LivingEntityRenderer<PiglinBossEntity, PiglinBossModel<PiglinBossEntity>> {
    public PiglinBossRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinBossModel<>(context.bakeLayer(PiglinBossModel.LAYER_LOCATION)), 2);
    }

    @Override
    public ResourceLocation getTextureLocation(PiglinBossEntity piglinBossEntity) {
        return ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/piglinboss/piglinboss.png");
    }

    // In your PiglinBossRenderer.java
    @Override
    protected float getFlipDegrees(PiglinBossEntity entity) {
        return 0f; // Prevents the vanilla 90-degree death rotation
    }

}
