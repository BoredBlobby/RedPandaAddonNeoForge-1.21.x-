package net.sussyit.redpandamod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.HedgehogEntity;

public class HedgehogModel<T extends HedgehogEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "hedgehogentity"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart spikes;
    private final ModelPart FrontLegR;
    private final ModelPart FrontLegL;
    private final ModelPart backLegL;
    private final ModelPart backLegR;

    public HedgehogModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.head = this.body.getChild("head");
        this.spikes = this.body.getChild("spikes");
        this.FrontLegR = this.root.getChild("FrontLegR");
        this.FrontLegL = this.root.getChild("FrontLegL");
        this.backLegL = this.root.getChild("backLegL");
        this.backLegR = this.root.getChild("backLegR");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -2.0F, -3.0F, 6.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-3.0F, -3.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-3.0F, 1.0F, -3.0F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 1.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(23, 20).addBox(-1.5F, -2.08F, -0.52F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(25, 13).addBox(-1.5F, 0.92F, -0.52F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 23).addBox(-1.5F, -0.38F, -0.82F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 7).addBox(-0.5F, -0.38F, -1.12F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 19).addBox(-2.5F, -1.08F, -0.52F, 5.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.08F, -2.68F));

        PartDefinition spikes = body.addOrReplaceChild("spikes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = spikes.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(2, 30).addBox(-0.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(1.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(3.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -3.0F, 0.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r2 = spikes.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 2.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r3 = spikes.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -2.0F, 2.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r4 = spikes.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.0F, 1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r5 = spikes.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, 0.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r6 = spikes.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -2.0F, 0.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r7 = spikes.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -1.0F, -1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r8 = spikes.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 0.0F, -2.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r9 = spikes.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.55F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -3.0F, 1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r10 = spikes.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.55F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 1.0F, 1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r11 = spikes.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.55F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, 1.0F, -1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r12 = spikes.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(2, 35).addBox(-1.0F, -2.0F, 0.55F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -3.0F, -1.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r13 = spikes.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(2, 30).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -2.0F, -2.0F, -1.0472F, 0.0F, -1.5708F));

        PartDefinition cube_r14 = spikes.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(2, 35).addBox(-0.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(1.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(3.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -3.0F, -2.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r15 = spikes.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(2, 35).addBox(-0.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(1.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 35).addBox(3.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -3.0F, 2.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r16 = spikes.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(2, 35).addBox(-0.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 35).addBox(-2.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -3.0F, 1.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r17 = spikes.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(2, 35).addBox(-0.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(2, 30).addBox(-2.5F, -1.9F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -3.0F, -1.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition cube_r18 = spikes.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(2, 35).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, 2.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r19 = spikes.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, 2.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r20 = spikes.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -1.0F, 1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r21 = spikes.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(2, 35).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, 0.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r22 = spikes.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, 0.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r23 = spikes.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(2, 35).addBox(0.0F, -2.0F, 0.6F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -3.0F, 1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r24 = spikes.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.6F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 1.0F, 1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r25 = spikes.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(2, 35).addBox(0.0F, -2.0F, 0.6F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 1.0F, -1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r26 = spikes.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.6F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -3.0F, -1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r27 = spikes.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(2, 35).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -1.0F, -1.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r28 = spikes.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.0F, -2.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition cube_r29 = spikes.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(2, 30).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 0.0F, -2.0F, -1.0472F, 0.0F, 1.5708F));

        PartDefinition FrontLegR = root.addOrReplaceChild("FrontLegR", CubeListBuilder.create().texOffs(4, 24).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -1.5F, -0.5F));

        PartDefinition FrontLegL = root.addOrReplaceChild("FrontLegL", CubeListBuilder.create().texOffs(24, 4).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -1.0F, -0.5F));

        PartDefinition backLegL = root.addOrReplaceChild("backLegL", CubeListBuilder.create().texOffs(0, 24).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -1.5F, 2.5F));

        PartDefinition backLegR = root.addOrReplaceChild("backLegR", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -1.5F, 2.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(HedgehogEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(HedgehogAnimations.ANIM_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}