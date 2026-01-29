package net.sussyit.redpandamod.entity.custom;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.sussyit.redpandamod.util.CameraShakeUtils;

import javax.swing.plaf.nimbus.State;

public class ThingEntity extends LivingEntity {

    private final NonNullList<ItemStack> handStacks = NonNullList.withSize(2, ItemStack.EMPTY); // Mainhand and offhand
    private final NonNullList<ItemStack> armorStacks = NonNullList.withSize(4, ItemStack.EMPTY); // 4 pieces of armor
    private final CameraShakeUtils cameraShakeUtils = new CameraShakeUtils();


    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(ThingEntity.class, EntityDataSerializers.INT);

    public static final int NORMAL_STATE = 0;
    public static final int SEC_NORMAL_STATE = 1;
    public static final int SMILE_STATE = 2;
    public static final int CREEPY_STATE = 3;
    public static final int FINAL_STATE = 4;
    public static final int EXPLODING_STATE = 5;

    public int getState() {
        return this.entityData.get(STATE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // This initializes the state so the game knows it exists
        builder.define(STATE, NORMAL_STATE);
    }

    public ThingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24D);

    }


    @Override
    public void knockback(double strength, double x, double z) {
        //prevents any knockback
    }

    @Override
    public boolean fireImmune() {
        return true; // Prevents all fire-type damage (fire, lava, magma, fireballs)
    }

    @Override
    public void push(double x, double y, double z) {
        //prevents any push
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return armorStacks;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if(slot.getType() ==  EquipmentSlot.Type.HAND)
            return handStacks.get(slot.getIndex()); // index 0 or 1
        else
            return armorStacks.get(slot.getIndex()); // index 0-3
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if(slot.getType() ==  EquipmentSlot.Type.HAND)
            handStacks.set(slot.getIndex(), stack); // index 0 or 1
        else
            armorStacks.set(slot.getIndex(), stack); // index 0-3
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    private int randomLook = 380;//this.random.nextInt(500);
    private int randomLookTimer = 0;
    private int lookTimer = 0;


    @Override
    public void aiStep() {
        super.aiStep();
        if(!this.level().isClientSide()) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 1005.0D); // 15 block detection
            int state = this.entityData.get(STATE); // gets the integer

            if(state == NORMAL_STATE && nearestPlayer != null) {
                randomLookTimer++;
                if(randomLookTimer == randomLook) {
                    this.entityData.set(STATE, SEC_NORMAL_STATE);
                    randomLook = 100;//this.random.nextInt(500);
                }
            } else if(state == SEC_NORMAL_STATE) {
                lookTimer++;
                if(lookTimer > 80) {
                    this.entityData.set(STATE, SMILE_STATE);
                    lookTimer = 0;
                }
            } else if (state == SMILE_STATE) {
                lookTimer++;
                if(lookTimer > 10) {
                    this.entityData.set(STATE, CREEPY_STATE);
                    lookTimer = 0;
                }
            } else if (state == CREEPY_STATE) {
                lookTimer++;
                if(lookTimer > 100) {
                    this.teleportRandom();
                    lookTimer = 0;
                    this.entityData.set(STATE, FINAL_STATE);
                }
            } else if (state == FINAL_STATE) {
                lookTimer++;
                 if(lookTimer > 120) {
                     Player player = this.level().getNearestPlayer(this, 256.0D);
                     if (player != null) {
                         this.teleport(); // This will put it behind the player
                         lookTimer = 0;
                         randomLookTimer = 0;
                         this.entityData.set(STATE, EXPLODING_STATE);
                     }
                }
            } else if (state == EXPLODING_STATE) {
                lookTimer++;
                if (lookTimer == 1) this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                if (lookTimer > 60) this.explode(); // Boom after 1.5 seconds
            }
        }
    }


    protected boolean teleport() {
        if (!this.level().isClientSide() && this.isAlive()) {
            Player player = this.level().getNearestPlayer(this, 100.0D);

            if (player != null) {
                Vec3 lookAngle = player.getLookAngle();

                // Try different distances (from 3.0 blocks back to 1.5 blocks back)
                // This loop tries 4 different spots behind the player
                for (double distance = 3.0; distance >= 1.5; distance -= 0.5) {
                    double targetX = player.getX() + (lookAngle.x * -distance);
                    double targetZ = player.getZ() + (lookAngle.z * -distance);
                    double targetY = player.getY();

                    // Check if this specific spot is safe before attempting
                    BlockPos targetPos = BlockPos.containing(targetX, targetY, targetZ);
                    if (!this.level().getBlockState(targetPos).blocksMotion()) {
                        if (this.teleport(targetX, targetY, targetZ)) {
                            return true; // Successfully found a spot and teleported!
                        }
                    }
                }

                // If we get here, all spots directly behind were blocked.
                // As a fallback, try a small random offset near the player so it doesn't fail entirely
                double fallbackX = player.getX() + (this.random.nextDouble() - 0.5) * 4.0;
                double fallbackZ = player.getZ() + (this.random.nextDouble() - 0.5) * 4.0;
                return this.teleport(fallbackX, player.getY(), fallbackZ);
            }
        }
        return false;
    }

    protected boolean teleportRandom() {
        if (!this.level().isClientSide() && this.isAlive()) {
            // Try 16 times to find a valid spot
            for(int i = 0; i < 16; ++i) {
                double d0 = this.getX() + (this.random.nextDouble() - 0.5) * 60.0;
                double d1 = this.getY() + (double)(this.random.nextInt(64) - 32);
                double d2 = this.getZ() + (this.random.nextDouble() - 0.5) * 60.0;

                BlockPos targetPos = new BlockPos((int)d0, (int)d1, (int)d2);

                // ONLY try to teleport if the random spot is technically "Air" (not blocking motion)
                // This ensures your helper function works correctly by scanning DOWN from air to floor.
                if (!this.level().getBlockState(targetPos).blocksMotion()) {
                    if (this.teleport(d0, d1, d2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(x, y, z);

        // Scan down to find the floor (prevents spawning in mid-air)
        while (mutablePos.getY() > this.level().getMinBuildHeight() && !this.level().getBlockState(mutablePos).blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(mutablePos);
        if (blockstate.blocksMotion() && !blockstate.getFluidState().is(FluidTags.WATER)) {
            // Move Y up by 1 so the entity stands ON the block, not IN it
            double finalY = mutablePos.getY() + 1;

            EntityTeleportEvent.EnderEntity event = EventHooks.onEnderTeleport(this, x, finalY, z);
            if (event.isCanceled()) return false;

            Vec3 oldPos = this.position();

            // Use randomTeleport with the specific coordinates from the event
            boolean success = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);

            if (success) {
                // Sound at the old position
                this.level().playSound(null, oldPos.x, oldPos.y, oldPos.z, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                // Sound at the new position
                this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                return true;
            }
        }
        return false;
    }

    private void explode() {
        if (!this.level().isClientSide) {
            // Power 3.0F is a normal Creeper. 6.0F is a Charged Creeper.
            float explosionPower = 12.0F;

            // This triggers the actual explosion
            // Params: Source Entity, X, Y, Z, Power, Interaction Mode
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), explosionPower, Level.ExplosionInteraction.NONE);

            // Make sure to remove the entity after it blows up!
            this.discard();
        }
    }


    @Override
    public void tick() {
        super.tick();

        // Do this on both sides if you want it to look perfectly smooth
        // without waiting for server packets
        Player player = this.level().getNearestPlayer(this, 100.0D);

        if (player != null) {
            // Calculate the exact vector to the player's eyes
            this.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition());

            // Stop the "lag" by setting old rotation = new rotation
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();

            // Force the head and body variables
            this.yHeadRot = this.getYRot();
            this.yHeadRotO = this.getYRot();
            this.yBodyRot = this.getYRot();
            this.yBodyRotO = this.getYRot();
        }

        int state = this.entityData.get(STATE);
        if(this.level().isClientSide()) {
            if(state == SEC_NORMAL_STATE) {
                CameraShakeUtils.forcePlayerLookAt(player, this);
                CameraShakeUtils.shake(5, 0.5f, true, -0.25f);
            }
            else if(state == SMILE_STATE) {
                CameraShakeUtils.shake(5, 1.5f, true, -0.5f);
            } else if(state == CREEPY_STATE) {
                CameraShakeUtils.shake(5, 1.5f, true, -0.5f);

            }
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
        // This stops the client-side smoothing for position and rotation
        super.lerpTo(x, y, z, yaw, pitch, posRotationIncrements);
        this.setYRot(yaw);
        this.setXRot(pitch);
    }

    @Override
    public void lerpHeadTo(float yaw, int pitch) {
        // This stops the head-specific smoothing
        this.yHeadRot = yaw;
        this.yHeadRotO = yaw;
    }

    @Override
    public void setYBodyRot(float offset) {
        // Force the body to always match the actual rotation
        super.setYBodyRot(this.getYRot());
    }

    @Override
    public void setYHeadRot(float offset) {
        // Force the head to always match the actual rotation
        super.setYHeadRot(this.getYRot());
    }
}
