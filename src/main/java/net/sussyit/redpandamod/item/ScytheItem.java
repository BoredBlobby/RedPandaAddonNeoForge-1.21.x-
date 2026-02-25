package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ScytheItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    public ScytheItem(Properties properties) {
        super(properties);
    }

    // Only idle animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, "controller", 0, this::predicate)
                        .setAnimationSpeed(0.5d)
        );
    }

    private PlayState predicate(AnimationState<ScytheItem> event) {
        event.getController().setAnimation(
                RawAnimation.begin().thenLoop("idle")
        );
        return PlayState.CONTINUE;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private ScytheRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new ScytheRenderer();
                }
                return renderer;
            }
        });
    }

    @SuppressWarnings("removal")
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {

            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack,
                                                   LocalPlayer player,
                                                   HumanoidArm arm,
                                                   ItemStack itemInHand,
                                                   float partialTick,
                                                   float equipProcess,
                                                   float swingProcess) {

                poseStack.translate(0.5f, -0.6f, -1.0f);
                poseStack.mulPose(Axis.XP.rotationDegrees(-5f));
                return true;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}