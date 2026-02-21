package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ScytheRenderer extends GeoItemRenderer<ScytheItem> {
    public ScytheRenderer() {
        super(new ScytheModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ScytheItem animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, int colour) {

        // Check if we are NOT in first person
        boolean isFirstPerson = this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        // Hide the model's arm bones if we aren't in first person
        // Replace "left_arm" and "right_arm" with your actual bone names from Blockbench
        if (this.model != null) {
            this.getGeoModel().getBone("left_arm").ifPresent(bone -> bone.setHidden(!isFirstPerson));
            this.getGeoModel().getBone("right_arm").ifPresent(bone -> bone.setHidden(!isFirstPerson));
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }



}