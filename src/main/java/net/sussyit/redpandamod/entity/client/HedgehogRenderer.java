package net.sussyit.redpandamod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.HedgehogEntity;

public class HedgehogRenderer extends MobRenderer<HedgehogEntity, HedgehogModel<HedgehogEntity>> {

    private static final ResourceLocation REG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/hedgehog/hedgehog.png");
    private static final ResourceLocation BLUE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "textures/entity/hedgehog/hedgehogblue.png");

    public HedgehogRenderer(EntityRendererProvider.Context context) {
        super(context, new HedgehogModel<>(context.bakeLayer(HedgehogModel.LAYER_LOCATION)), 0.3f);
    }


    @Override
    public ResourceLocation getTextureLocation(HedgehogEntity hedgehogEntity) {
        if(hedgehogEntity.getSkinID() == 0) {
            return REG_TEXTURE;
        } else {
            return BLUE_TEXTURE;
        }
    }

    @Override
    public void render(HedgehogEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            pPoseStack.scale(1f, 1f, 1f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
