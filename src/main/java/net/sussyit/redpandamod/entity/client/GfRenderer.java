package net.sussyit.redpandamod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.GfVariant;
import net.sussyit.redpandamod.entity.custom.GfEntity;
import net.sussyit.redpandamod.entity.custom.HedgehogEntity;

public class GfRenderer extends MobRenderer<GfEntity, GfModel<GfEntity>> {

    private static final ResourceLocation GF1_PHASE1 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf1.png");
    private static final ResourceLocation GF2_PHASE1 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf2.png");
    private static final ResourceLocation GF3_PHASE1 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf3.png");

    private static final ResourceLocation GF1_PHASE2 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf1p2.png");
    private static final ResourceLocation GF2_PHASE2 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf2p2.png");
    private static final ResourceLocation GF3_PHASE2 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gf3p2.png");

    private static final ResourceLocation GF_PHASE3 =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/gf/gfp3.png");

    public GfRenderer(EntityRendererProvider.Context context) {
        super(context, new GfModel<>(context.bakeLayer(GfModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GfEntity gfEntity) {
        if(gfEntity.getVar() == GfVariant.KOREAN) {
            if(gfEntity.getPhase() == GfEntity.REVEAL) {
                return GF_PHASE3;
            } else if (gfEntity.getPhase() == GfEntity.LEAKING){
                return GF1_PHASE2;
            } else {
                return  GF1_PHASE1;
            }

        }
        if(gfEntity.getVar() == GfVariant.REINDEER) {
            if(gfEntity.getPhase() == GfEntity.REVEAL) {
                return GF_PHASE3;
            } else if (gfEntity.getPhase() == GfEntity.LEAKING) {
                return GF2_PHASE2;
            } else {
                return  GF2_PHASE1;
            }
        } else {
            if(gfEntity.getPhase() == GfEntity.REVEAL) {
                return GF_PHASE3;
            } else if (gfEntity.getPhase() == GfEntity.LEAKING) {
                return GF3_PHASE2;
            } else {
                return  GF3_PHASE1;
            }
        }
    }

    @Override
    public void render(GfEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            pPoseStack.scale(1f, 1f, 1f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
