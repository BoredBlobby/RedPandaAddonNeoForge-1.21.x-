package net.sussyit.redpandamod.entity.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PiglinBossEntity extends LivingEntity {
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState awakeningAnimationState = new AnimationState();

    private final NonNullList<ItemStack> handStacks = NonNullList.withSize(2, ItemStack.EMPTY); // Mainhand and offhand
    private final NonNullList<ItemStack> armorStacks = NonNullList.withSize(4, ItemStack.EMPTY); // 4 pieces of armor

    public PiglinBossEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.FOLLOW_RANGE, 24D);

    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return armorStacks;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot)
    {
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

    /* States */
    // EntityDataAccessor: type of the "key" that unlocks an Integer calue inside an enityt's data folder
    //PiglinBossEneity.class tells the game which entity this dat belongs to
    //EnetiyDatSerializer.INT tells the game how to turn this data into "bits to sent it over th eintenert
    /* Stores all the current states of the mob into a folder that can be called later on for client side */
    private static final EntityDataAccessor<Integer> BOSS_STATE =
            SynchedEntityData.defineId(PiglinBossEntity.class, EntityDataSerializers.INT);

    public static final int SLEEPING = 0;
    public static final int AWAKENING = 1; // Playing the "wake up" animation
    public static final int FIGHTING = 2;

    /* Persistent data versus One-Time Signals: The game needs to remmebr what the boss was doing */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // This initializes the state so the game knows it exists
        builder.define(BOSS_STATE, SLEEPING);
    }

    /*Checks the distance tot eh player every second */

    private int sleepTimer = 0; // Internal timer for the 10-second rule

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide()) { // server side
            Player nearestPlayer = this.level().getNearestPlayer(this, 15.0D); // 15 block detection

            int state = this.entityData.get(BOSS_STATE); // gets the integer

            if (state == SLEEPING && nearestPlayer != null) { // if state is SLEEPING and there is a player
                this.entityData.set(BOSS_STATE, AWAKENING); //set to awakening
                this.sleepTimer = 0;
            }
            else if(state == AWAKENING) {
                this.sleepTimer++;
                if (this.sleepTimer >= 100) {
                    this.entityData.set(BOSS_STATE, FIGHTING);
                    this.sleepTimer = 0;
                }
            }
            else if (state == FIGHTING && nearestPlayer == null) {
                // Player left range: Start health regen and timer
                this.setHealth(this.getHealth() + 0.1f); // Slow regen
                this.sleepTimer++;

                if (this.sleepTimer >= 200) { // 200 ticks = 10 seconds
                    this.entityData.set(BOSS_STATE, SLEEPING);
                }
            }
            else if (nearestPlayer != null) {
                this.sleepTimer = 0; // Reset timer if player comes back
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            int state = this.entityData.get(BOSS_STATE);

            if (state == SLEEPING) {
                this.sleepAnimationState.startIfStopped(this.tickCount);
                this.awakeningAnimationState.stop();
            } else if (state == AWAKENING) {
                this.sleepAnimationState.stop();
                this.awakeningAnimationState.startIfStopped(this.tickCount);
            }
        }
    }

    /* Animations */
    // 1. TRIGGER: Start the animation when the Client knows the mob is dead.
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) { // '3' is the internal Minecraft ID for "Entity Died"
            // We use 'this.tickCount' as the start time
            this.deathAnimationState.start(this.tickCount);
        }
        super.handleEntityEvent(id);
    }

    // 2. DURATION: Keep the body in the world long enough for the animation to finish.
    @Override
    protected void tickDeath() {
        // Increases the death timer every tick
        ++this.deathTime;

        // Example: If your animation is 3 seconds long (60 ticks), change 20 to 60.
        // The default Minecraft death time is 20 ticks (1 second).
        if (this.deathTime == 60 && !this.level().isClientSide()) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(PiglinBossEntity.this);
        }
    }
}
