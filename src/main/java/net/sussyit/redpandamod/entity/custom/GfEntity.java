package net.sussyit.redpandamod.entity.custom;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.sussyit.redpandamod.effect.ModEffects;
import net.sussyit.redpandamod.entity.GfVariant;
import net.sussyit.redpandamod.item.ModItems;

import javax.annotation.Nullable;


public class GfEntity extends Wolf {
    public int drinkCooldown = 0;
    public final AnimationState revealAnimationState = new AnimationState();
    public final AnimationState bobaAnimationState = new AnimationState();
    private static final EntityDataAccessor<Integer> PHASE =
            SynchedEntityData.defineId(GfEntity.class, EntityDataSerializers.INT);
    public static final int NORMAL = 0;
    public static final int DRINKING = 1;
    public static final int LEAKING = 2;
    public static final int REVEAL = 3;
    public static final int MALFUNCTION = 4;

    public int getPhase() {
        return this.entityData.get(PHASE);
    }

    private void setPhase(int phase) {
        this.entityData.set(PHASE, phase);
    }

    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(GfEntity.class, EntityDataSerializers.INT);

    public int getTypeVar() {
        return this.entityData.get(VARIANT);
    }

    public GfVariant getVar() {
        return GfVariant.byId(this.getTypeVar() & 255);
    }

    private void setVar(GfVariant var) {
        this.entityData.set(VARIANT, var.getId() & 255);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHASE, NORMAL);
        builder.define(VARIANT, 0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (PHASE.equals(key)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(key);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Phase", this.getPhase());
        compound.putInt("Variant", this.getTypeVar());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(VARIANT, compound.getInt("Variant"));
        this.setPhase(compound.getInt("Phase"));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        GfVariant variant = Util.getRandom(GfVariant.values(), this.random);
        this.setVar(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public GfEntity(EntityType<? extends Wolf> entityType, Level level) {
        super(entityType, level);
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    public void tick() {
        if(this.level().isClientSide()) {
            if(this.getPhase() == DRINKING) {
                this.bobaAnimationState.startIfStopped(this.tickCount);
            } else if (this.getPhase() == MALFUNCTION || this.getPhase() == LEAKING){
                this.revealAnimationState.startIfStopped(this.tickCount);
            }
            else {
                this.bobaAnimationState.stop();
            }
        }
        super.tick();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
            if(this.getPhase() == DRINKING) {
                if(drinkCooldown == 90) {
                    this.setPhase(NORMAL);
                    drinkCooldown = 0;
                }
                drinkCooldown++;
                this.setDeltaMovement(0, 0, 0);
            } if (this.getPhase() == MALFUNCTION) {
                if (drinkCooldown == 18) {
                    this.setPhase(LEAKING);
                    drinkCooldown = 0;
                }
                drinkCooldown ++;
                this.setDeltaMovement(0, 0, 0);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (!this.level().isClientSide) {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getPhase() == NORMAL) {
                    this.setPhase(DRINKING);
                    FoodProperties foodproperties = itemstack.getFoodProperties(this);
                    float f = foodproperties != null ? (float) foodproperties.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    itemstack.consume(1, player);
                    this.gameEvent(GameEvent.EAT);
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                } else if (itemstack.is(Items.SHEARS) && !this.level().isClientSide && this.getPhase() != REVEAL) {
                    if(this.getPhase() == LEAKING) {
                        this.setPhase(REVEAL);
                    } else {
                        this.setPhase(MALFUNCTION);
                    }
                    return InteractionResult.SUCCESS;
                } else {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if (!interactionresult.consumesAction() && this.isOwnedBy(player)) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((LivingEntity) null);
                        return InteractionResult.SUCCESS_NO_ITEM_USED;
                    } else {
                        return interactionresult;
                    }
                }
            } else if (itemstack.is(ModItems.CHOCOLATEBOX) && !this.isAngry()) {
                itemstack.consume(1, player);
                this.tryToTame(player);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(player, hand);
            }
        } else {
            boolean flag = this.isOwnedBy(player) || this.isTame() || itemstack.is(ModItems.CHOCOLATEBOX) && !this.isTame() && !this.isAngry();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }

    private void tryToTame(Player player) {
        if(!player.hasEffect(ModEffects.PERFORMATIVE_EFFECT)) {
            this.level().broadcastEntityEvent(this, (byte)6);
            return; // Exit the method immediately so no taming happens
        }
        if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
            this.tame(player);
            this.navigation.stop();
            this.setTarget((LivingEntity)null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }

    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITH_ITEM;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.MATCHABOBA);
    }
}
