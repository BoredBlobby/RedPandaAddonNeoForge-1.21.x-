package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;


public class ScytheRenderer extends GeoItemRenderer<ScytheItem> {
    public ScytheRenderer() {
        super(new ScytheModel());
    }

    private float equipProgress = 0f;
    private float prevEquipProgress = 0f;

    @Override
    public void actuallyRender(PoseStack poseStack, ScytheItem animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, int colour) {

        // Check if we are NOT in first person
        boolean isFirstPerson = this.renderPerspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                this.renderPerspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        // Smoothly interpolate equip progress
        float target = isFirstPerson ? 1f : 0f; // 1 = fully pulled out, 0 = not equipped
        float smoothing = 0.02f;
        equipProgress += (target - equipProgress) * smoothing; // smoothing factor

        float startDistanceX = 3.0f; // how far down it starts (increase for more dramatic pull)

        float startDistanceY = 3.0f;

        float equipOffset = 0.0f + equipProgress;

        poseStack.translate(0.0F, (-startDistanceX * equipOffset) + 1.2, (-startDistanceY * equipOffset) + 1.45);
        // Hide the model's arm bones if we aren't in first person
        // Replace "left_arm" and "right_arm" with your actual bone names from Blockbench
        if (this.model != null) {
            this.getGeoModel().getBone("left_arm").ifPresent(bone -> bone.setHidden(!isFirstPerson));
            this.getGeoModel().getBone("right_arm").ifPresent(bone -> bone.setHidden(!isFirstPerson));
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

}