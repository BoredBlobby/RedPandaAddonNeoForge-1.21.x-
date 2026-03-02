package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ScytheItem extends SwordItem implements GeoItem {

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    public ScytheItem(Tier tier, Properties properties) {
        super(tier, properties);
    }


    // Only idle animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                // The main idle/movement controller
                new AnimationController<>(this, "base_controller", 0, state -> {
                    return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
                })
        );

        controllers.add(
                // Dedicated attack controller
                new AnimationController<>(this, "attack_controller", 0, state -> PlayState.STOP)
                        .triggerableAnim("attack", RawAnimation.begin().thenPlay("attack"))
        );

        controllers.add(
                // Dedicated attack controller
                new AnimationController<>(this, "trick_controller", 5, state -> PlayState.STOP)
                        .triggerableAnim("trick", RawAnimation.begin().thenPlay("trick"))
        );
    }

    private PlayState predicate(AnimationState<ScytheItem> event) {
        event.getController().setAnimation(
                RawAnimation.begin().thenLoop("idle")
        );
        return PlayState.CONTINUE;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.Entity entity) {
        if (player.level().isClientSide) {
            // Trigger the "attack" animation defined in the controller
            triggerAnim(player, GeoItem.getId(stack), "attack_controller", "attack");
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    // To trigger when swinging at the air:
    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        // Check if we are on the client AND if it's the main hand
        if (level.isClientSide && hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "trick_controller", "trick");
        }
        return super.use(level, player, hand);
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