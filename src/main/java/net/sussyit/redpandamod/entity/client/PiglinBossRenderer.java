package net.sussyit.redpandamod.entity.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;


public class PiglinBossRenderer extends LivingEntityRenderer<PiglinBossEntity, PiglinBossModel<PiglinBossEntity>> {

    // Define your textures clearly
    private static final ResourceLocation PHASE_1_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/piglinboss/piglinboss.png");
    private static final ResourceLocation PHASE_2_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/piglinboss/piglinbosssecphase.png");

    public PiglinBossRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinBossModel<>(context.bakeLayer(PiglinBossModel.LAYER_LOCATION)), 2);
    }

    @Override
    public ResourceLocation getTextureLocation(PiglinBossEntity entity) {
        // 1. If we are definitely in Phase 1, use Phase 1 texture
        if(entity.getBossPhase() == PiglinBossEntity.PHASE_2 && ((entity.getBossState() == PiglinBossEntity.ROAR) || entity.getBossState() == PiglinBossEntity.FIGHTING)) {
            return PHASE_2_TEXTURE;
        }
        return PHASE_1_TEXTURE;
    }

    @Override
    protected float getFlipDegrees(PiglinBossEntity entity) {
        return 0f;
    }
}
