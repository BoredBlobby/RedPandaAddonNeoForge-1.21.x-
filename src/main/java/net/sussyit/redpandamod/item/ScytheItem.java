package net.sussyit.redpandamod.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ScytheItem extends SwordItem implements GeoItem {
    private static final java.util.Map<java.util.UUID, Integer> SPIN_TICKS = new java.util.concurrent.ConcurrentHashMap<>();
    // Tracks if the player is currently holding the scythe to play the equip sound once
    private static final java.util.Map<java.util.UUID, Boolean> WAS_EQUIPPED = new java.util.concurrent.ConcurrentHashMap<>();

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
    /*
    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        // Check if we are on the client AND if it's the main hand
        if (level.isClientSide && hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            triggerAnim(player, GeoItem.getId(player.getItemInHand(hand)), "trick_controller", "trick");
        }
        return super.use(level, player, hand);
    }
    */
    // Triggered when right-clicking:
    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Client-side: Play the animation
        if (level.isClientSide && hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            triggerAnim(player, GeoItem.getId(stack), "trick_controller", "trick");
        }

        // Server-side: Start the damage sequence
        if (!level.isClientSide && hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            // Set the spin to last for 20 ticks (1 second). Adjust this to match your animation length!
            SPIN_TICKS.put(player.getUUID(), 50);

            // Put the item on cooldown so the player can't spam it
            player.getCooldowns().addCooldown(this, 60);
        }

        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // Runs every tick while the item is in the player's inventory
    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (!level.isClientSide && entity instanceof net.minecraft.world.entity.player.Player player) {
            java.util.UUID uuid = player.getUUID();

            // Check if this player is currently in a spinning state
            if (SPIN_TICKS.containsKey(uuid)) {
                int ticks = SPIN_TICKS.get(uuid);

                if (ticks > 0) {
                    // Deal damage every 5 ticks (resulting in 4 total hits over a 20-tick spin)
                    if(ticks < 40) {
                        if (ticks % 1 == 0) {
                            performSpinDamage(level, player);
                        }
                    }
                        // Tick down the timer
                        SPIN_TICKS.put(uuid, ticks - 1);
                } else {
                    // The spin is over, remove them from the tracker
                    SPIN_TICKS.remove(uuid);
                }
            }
        }
    }

    // Helper method to handle the Area of Effect damage
    private void performSpinDamage(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player) {
        // Create an invisible 3-block radius box around the player
        net.minecraft.world.phys.AABB hitBox = player.getBoundingBox().inflate(3.0D, 1.0D, 3.0D);
        java.util.List<net.minecraft.world.entity.LivingEntity> targets = level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, hitBox);

        for (net.minecraft.world.entity.LivingEntity target : targets) {
            // Damage everything in the radius EXCEPT the player doing the spinning
            if (target != player && target.isAlive()) {
                // Deal 4.0 damage (2 hearts) per hit. Adjust as needed.
                target.hurt(level.damageSources().playerAttack(player), 2.0F);

                // Pushes enemies slightly outward for extra impact
                target.knockback(0.4D, player.getX() - target.getX(), player.getZ() - target.getZ());
            }
        }
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