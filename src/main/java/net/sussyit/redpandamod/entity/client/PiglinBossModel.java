package net.sussyit.redpandamod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

public class PiglinBossModel<T extends PiglinBossEntity> extends HierarchicalModel<T> {


    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, "piglinbossentity"), "main");
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart Potion;
    private final ModelPart cap;
    private final ModelPart liquid;
    private final ModelPart cubeofdoom;

    public PiglinBossModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body = this.root.getChild("body");
        this.Potion = this.body.getChild("Potion");
        this.cap = this.Potion.getChild("cap");
        this.liquid = this.Potion.getChild("liquid");
        this.cubeofdoom = this.body.getChild("cubeofdoom");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -5.0F, -8.0F, 16.0F, 24.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

        PartDefinition Potion = body.addOrReplaceChild("Potion", CubeListBuilder.create().texOffs(0, 40).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 8).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.5F, -2.0F));

        PartDefinition cap = Potion.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition liquid = Potion.addOrReplaceChild("liquid", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));

        PartDefinition cubeofdoom = body.addOrReplaceChild("cubeofdoom", CubeListBuilder.create().texOffs(16, 47).addBox(10.5F, -145.5F, 88.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(11.5F, -146.5F, 87.5F, 3.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(36, 41).addBox(9.5F, -144.5F, 87.5F, 7.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-13.5F, 148.5F, -94.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(PiglinBossEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.deathAnimationState, PiglinBossAnimations.ANIM_DEATH, ageInTicks, 1f);
        this.animate(entity.sleepAnimationState, PiglinBossAnimations.ANIM_SLEEP, ageInTicks, 1f);
        this.animate(entity.awakeningAnimationState, PiglinBossAnimations.ANIM_AWAKENING, ageInTicks, 1f);
        this.animate(entity.attackFireShieldAnimationState, PiglinBossAnimations.ANIM_ATTACK_FIRE_SHIELD, ageInTicks, 1f);
        this.animate(entity.attackShieldSpinAnimationState, PiglinBossAnimations.ANIM_ATTACK_SHIELD_SPIN, ageInTicks, 1f);
        this.animate(entity.attackEarthQuakeAnimationState, PiglinBossAnimations.ANIM_ATTACK_EARTHQUAKE, ageInTicks, 1f);
        this.animate(entity.attackShadowVoidAnimationState, PiglinBossAnimations.ANIM_ATTACK_SHADOW_VOID, ageInTicks, 1f);
        this.animate(entity.attackVoidCallAnimationState, PiglinBossAnimations.ANIM_ATTACK_VOID_CALL, ageInTicks, 1f);
        this.animate(entity.revivalAnimationState, PiglinBossAnimations.ANIM_REVIVAL, ageInTicks, 1f);
        this.animate(entity.roarAnimationState, PiglinBossAnimations.ANIM_ROAR, ageInTicks, 1f);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
