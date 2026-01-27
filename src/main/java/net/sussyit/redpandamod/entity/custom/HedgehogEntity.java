package net.sussyit.redpandamod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.sussyit.redpandamod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class HedgehogEntity extends Animal {
    int inflateCounter = 1;
    int deflateTimer = 0;
    static final TargetingConditions targetingConditions;
    private static final EntityDataAccessor<Integer> MOOD =
            SynchedEntityData.defineId(HedgehogEntity.class, EntityDataSerializers.INT);
    private static final Predicate<LivingEntity> SCARY_MOB;
    public static final int CALM = 0;
    public static final int AGRESSIVE = 1;

    public final AnimationState walkingAnimationState = new AnimationState();
    public HedgehogEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public int getMood() {
        return this.entityData.get(MOOD);
    }

    public void setmood(int mood) {
        this.entityData.set(MOOD, mood);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOOD, CALM);
        builder.define(skinID, 0);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (MOOD.equals(key)) {
            this.refreshDimensions();
        }
        if(skinID.equals(key)) {
            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(key);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Mood", this.getMood());
        compound.putInt("skinId", this.getSkinID());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setmood(Math.min(compound.getInt("Mood"), 0));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, stack -> stack.is(Items.OAK_LEAVES), false));

        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.0));

        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24D);
    }

    public void tick() {
        if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
            if (this.inflateCounter > 0) {
                if (this.getMood() == 0) {
                    this.setmood(1);
                    inflateCounter = 0;
                }
                ++this.inflateCounter;
            } else if (this.getMood() != 0) {
                if (this.deflateTimer > 60 && this.getMood() == 1) {
                    this.setmood(0);
                    deflateTimer = 0;
                }

                ++this.deflateTimer;
            }
        }

        super.tick();
    }


    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.getMood() > 0) {
            Iterator var1 = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3),
                    mob -> targetingConditions.test(this, mob)).iterator();
            while(var1.hasNext()) {
                Mob mob = (Mob)var1.next();
                if (mob.isAlive()) {
                    this.touch(mob);
                }
            }
        }
    }

    private void touch(Mob mob) {
        int i = this.getMood();
        if (mob.hurt(this.damageSources().mobAttack(this), (float)(1 + i))) {
            this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    @Override
    public void playerTouch(Player entity) {
        int i = this.getMood();
        if (entity instanceof ServerPlayer && i > 0 && entity.hurt(this.damageSources().mobAttack(this), (float)(1 + i))) {
            if (!this.isSilent()) {
                ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            }
        }
    }

    static {
        SCARY_MOB = (p_348288_) -> {
            if (p_348288_ instanceof Player player) {
                if (player.isCreative()) {
                    return false;
                }
            }

            return !p_348288_.getType().is(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
        };
        targetingConditions = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(SCARY_MOB);
    }


    private static final EntityDataAccessor<Integer> skinID =
            SynchedEntityData.defineId(HedgehogEntity.class, EntityDataSerializers.INT);

    public void changeBlue() {
        this.entityData.set(skinID, 1);
    }

    public int getSkinID() {
        return this.entityData.get(skinID);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Replace 'Items.APPLE' with your specific item
        if (itemstack.is(ModItems.GOLD_RING) && !this.level().isClientSide) {

            changeBlue();

            // 2. Change the Movement Speed
            // This adds a permanent modifier to the speed attribute
            AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speedAttribute != null) {
                // Clear old modifiers if you want to prevent stacking
                speedAttribute.removeModifier(ResourceLocation.fromNamespaceAndPath("modid", "hedgehog_speed"));

                speedAttribute.addPermanentModifier(new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("modid", "hedgehog_speed"),
                        0.5f,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }

            // Consume the item
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }


    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(Items.BAMBOO);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }
}
