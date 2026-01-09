package net.sussyit.redpandamod.entity.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sussyit.redpandamod.entity.client.*;
import net.sussyit.redpandamod.util.CameraShakeUtils;

public class PiglinBossEntity extends LivingEntity {
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState awakeningAnimationState = new AnimationState();
    public final AnimationState attackFireShieldAnimationState = new AnimationState();
    public final AnimationState attackShieldSpinAnimationState = new AnimationState();
    public final AnimationState attackEarthQuakeAnimationState = new AnimationState();

    private final ServerBossEvent bossEvent =
            new ServerBossEvent(Component.literal("Piglin Boss"), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private final FissureManager fissureManager = new FissureManager();
    private final NonNullList<ItemStack> handStacks = NonNullList.withSize(2, ItemStack.EMPTY); // Mainhand and offhand
    private final NonNullList<ItemStack> armorStacks = NonNullList.withSize(4, ItemStack.EMPTY); // 4 pieces of armor

    private final CameraShakeUtils cameraShakeUtils = new CameraShakeUtils();

    public PiglinBossEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.setHealth(this.getMaxHealth()); // Ensures it starts at 300
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 300D)
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

    /* Boss Bar */

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.bossEvent.addPlayer(serverPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.bossEvent.removePlayer(serverPlayer);
    }


    /* States */
    // EntityDataAccessor: type of the "key" that unlocks an Integer calue inside an enityt's data folder
    //PiglinBossEneity.class tells the game which entity this data belongs to
    //EnetiyDatSerializer.INT tells the game how to turn this data into "bits to sent it over th eintenert
    /* Stores all the current states of the mob into a folder that can be called later on for client side */
    private static final EntityDataAccessor<Integer> BOSS_STATE =
            SynchedEntityData.defineId(PiglinBossEntity.class, EntityDataSerializers.INT);

    public static final int SLEEPING = 0;
    public static final int AWAKENING = 1; // Playing the "wake up" animation
    public static final int FIGHTING = 2;
    public static final int DEATH = 3;

    /* Persistent data versus One-Time Signals: The game needs to remmebr what the boss was doing */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // This initializes the state so the game knows it exists
        builder.define(BOSS_STATE, SLEEPING);
        builder.define(BOSS_PHASE, PHASE_1);
        builder.define(CURRENT_ATTACK, ATTACK_NONE);
    }

    /*Checks the distance tot eh player every second */

    private int sleepTimer = 0; // Internal timer for the 10-second rule

    private IBossAttack activeAttack = null;
    private int attackCooldown = 0;

    @Override
    public void aiStep() {
        super.aiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth()); //boss health bar
        if (!this.level().isClientSide()) { // server side
            Player nearestPlayer = this.level().getNearestPlayer(this, 15.0D); // 15 block detection
            Player nearestPlayerLeave = this.level().getNearestPlayer(this, 45.0D); // 30 block detection

            int state = this.entityData.get(BOSS_STATE); // gets the integer

            if (state == SLEEPING && nearestPlayer != null) { // if state is SLEEPING and there is a player
                this.entityData.set(BOSS_STATE, AWAKENING); //set to awakening
                this.sleepTimer = 0;
                this.level().broadcastEntityEvent(this, (byte) 64); // trigger roar(server side)
            }
            else if(state == AWAKENING) {
                this.sleepTimer++;
                if (this.sleepTimer >= 100) {
                    this.entityData.set(BOSS_STATE, FIGHTING);
                    this.sleepTimer = 0; //BE AWARE: using sleep timer; careful of interference
                }
            }
            /* Attack mode START */
            else if (state == FIGHTING && nearestPlayerLeave != null) {
                if(this.level().getNearestPlayer(this, 25.0D) == null) {
                    this.setHealth(this.getHealth() + 0.3f); // Slow regen
                }
                if (this.activeAttack != null) {
                    this.activeAttack.tick(this);

                    if (this.activeAttack.isFinished()) {
                        this.activeAttack.stop(this);
                        this.activeAttack = null;
                        this.setAttackState(ATTACK_NONE); // Reset animation
                        this.attackCooldown = 60; // 3 seconds rest between attacks
                    }
                    return; // Don't do anything else while attacking
                }

                // 2. Logic to choose a new attack (if cooldown is over)
                if (this.attackCooldown > 0) {
                    this.attackCooldown--;
                } else {
                    startNewAttack();
                }
            }
            /*Attack mode END */
            else if (state == FIGHTING && nearestPlayerLeave == null) {
                // Player left range: Start health regen and timer
                this.setHealth(this.getHealth() + 0.10f); // Slow regen
                this.sleepTimer++;

                if (this.sleepTimer >= 200) { // 200 ticks = 10 seconds
                    this.entityData.set(BOSS_STATE, SLEEPING);
                }
            }
            else if (nearestPlayerLeave != null) {
                this.sleepTimer = 0; // Reset timer if player comes back
            }
        }
    }

    @Override
    public void tick() {


        this.fissureManager.tick(this.level());
        super.tick();
        if (this.level().isClientSide()) {
            int state = this.entityData.get(BOSS_STATE);

            if (state == SLEEPING) {
                this.sleepAnimationState.startIfStopped(this.tickCount);
                this.awakeningAnimationState.stop();
            } else if (state == AWAKENING) {
                this.sleepAnimationState.stop();
                this.awakeningAnimationState.startIfStopped(this.tickCount);
            } else if (state == FIGHTING) {
                this.awakeningAnimationState.stop();
                int attackID = this.entityData.get(CURRENT_ATTACK);
                if(attackID == ATTACK_SHIELD_SPIN) {
                    this.attackShieldSpinAnimationState.startIfStopped(this.tickCount);
                }
                else if(attackID == ATTACK_FIRE_SHIELD) {
                    this.attackFireShieldAnimationState.startIfStopped(this.tickCount);
                } else if(attackID == ATTACK_EARTHQUAKE) {
                    this.attackEarthQuakeAnimationState.startIfStopped(this.tickCount);
                } else {
                    this.attackEarthQuakeAnimationState.stop();
                    this.attackFireShieldAnimationState.stop(); // stops if we are not doing an attack
                    this.attackShieldSpinAnimationState.stop();
                }
            }
        }
    }

    /* Animations */
    // 1. TRIGGER: Start the animation when the Client knows the mob is dead.
    @Override
    public void handleEntityEvent(byte id) { //Clientside
        if (id == 72) { // '3' is the internal Minecraft ID for "play hurt/death animation"
            // We use 'this.tickCount' as the start time
            this.deathAnimationState.start(this.tickCount);
        }
        else if (id == 64) {
            // 1. Play the Roar Sound
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 1.0f, 0.8f, false);

            // 2. Trigger Screen Shake (Example using vanilla particles or a mod's API)
            cameraShakeUtils.shake(70, 1f, true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    // 2. DURATION: Keep the body in the world long enough for the animation to finish.
    @Override
    protected void tickDeath() {
        // Increases the death timer every tick
        ++this.deathTime;

        // Example: If your animation is 3 seconds long (60 ticks), change 20 to 60.
        // The default Minecraft death time is 20 ticks (1 second).
        if (this.deathTime == 60 && !this.level().isClientSide()) {
            super.die(this.damageSources().generic());
            this.remove(RemovalReason.KILLED);
            this.dropExperience(PiglinBossEntity.this);
        }
    }

    /* Boss Phases */

    //Stored data for the mob into a certain folder that can be called back later
    // 1. The phases(1, 2, 3)
    private static final EntityDataAccessor<Integer> BOSS_PHASE =
            SynchedEntityData.defineId(PiglinBossEntity.class, EntityDataSerializers.INT);

    public static final int PHASE_1 = 1;
    public static final int PHASE_2 = 2;
    public static final int PHASE_3 = 3;

    //2. Specific attacks(1-9)
    private static final EntityDataAccessor<Integer> CURRENT_ATTACK =
            SynchedEntityData.defineId(PiglinBossEntity.class, EntityDataSerializers.INT);

    public static final int ATTACK_FIRE_SHIELD = 0;
    public static final int ATTACK_SHIELD_SPIN = 1;
    public static final int ATTACK_EARTHQUAKE = 2;

    public static final int ATTACK_SHADOW_VOID = 3;
    public static final int ATTACK_VOID_CALL = 4;
    public static final int ATTACK_SHIELD_BUBBLE = 5;

    public static final int ATTACK_UKNOWN6 = 6;
    public static final int ATTACK_UKNOWN7 = 7;
    public static final int ATTACK_UKNOWN8 = 8;

    public static final int ATTACK_NONE = 9;

    public void setAttackState(int currAttack) {
        this.entityData.set(CURRENT_ATTACK, currAttack);
    }

    private void startNewAttack() {
        int phase = this.entityData.get(BOSS_PHASE);
        Player nearestPlayer = this.level().getNearestPlayer(this, 10.0D);
        // Pick an attack based on the current phase
        if (phase == PHASE_1) {
            if(nearestPlayer != null) {
                int randomPick = this.random.nextInt(4);
                if (randomPick < 3) {
                    this.activeAttack = new ShieldSpinAttack();
                } else if (randomPick == 3){
                    this.activeAttack = new EarthquakeAttack();
                }
            } else {
                int randomPick = this.random.nextInt(4); // 0, 1, or 2
                if (randomPick < 3) {
                    this.activeAttack = new FireShieldAttack();
                } else if (randomPick == 3) {
                    this.activeAttack = new EarthquakeAttack();
                }
            }
        }
        else if (phase == PHASE_2) {
            // Phase 2 logic...
        }

        // Start the chosen attack
        if (this.activeAttack != null) {
            this.activeAttack.start(this);
        }
    }

    public void triggerEarthquake() {
        // Start 25 fissures within a 20 block radius
        this.fissureManager.startEarthquake(this, 5, 20);
    }

    @Override //Prevents any attacks during death animation
    public void die(DamageSource source) {
        int state = this.entityData.get(BOSS_STATE);
        if (state == DEATH) return;

        this.entityData.set(BOSS_STATE, DEATH);

        this.level().broadcastEntityEvent(this, (byte) 72);
    }
}
