package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ScytheItem extends Item implements GeoItem {
    // 1. The Cache (Stores animation state per item)
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // NEW: A simple tracker to know when we switch weapons
    private static boolean isHoldingScythe = false;

    public ScytheItem(Properties properties) {
        super(properties);
    }

    // 2. Register the Controller
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    // 3. The Logic (The "Draw" Animation)
    // This ensures the animation starts fresh every time the item is "new" to the hand
    private PlayState predicate(AnimationState<ScytheItem> event) {
        var controller = event.getController();

        // event.isFirstPersonRendering() is a good way to check if we should care about the pullOut
        // But the most reliable "switch" check is checking if the controller has no animation
        // or if we force it to play when the item "appears"

        if (controller.getAnimationState() == AnimationController.State.STOPPED) {
            controller.setAnimation(RawAnimation.begin().thenPlay("pullOut").thenLoop("idle"));
        }

        return PlayState.CONTINUE;
    }

    // Inside your ScytheItem class
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ScytheRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new ScytheRenderer();

                return this.renderer;
            }
        });
    }

    @SuppressWarnings("removal")
    @Override

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private ScytheRenderer renderer;

            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
                                                        ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                poseStack.translate(0.5f, -0.6f, -1.0f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-5f));


                return true;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
