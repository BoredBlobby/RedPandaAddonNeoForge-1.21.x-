package net.sussyit.redpandamod.entity.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.sussyit.redpandamod.RedPandaMod;

public class PiglinBossEntity extends LivingEntity {
    public final AnimationState deathAnimationState = new AnimationState();

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
