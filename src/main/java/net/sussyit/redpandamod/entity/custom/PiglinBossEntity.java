package net.sussyit.redpandamod.entity.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PiglinBossEntity extends LivingEntity {
    private final NonNullList<ItemStack> handStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorStacks = NonNullList.withSize(4, ItemStack.EMPTY);

    public PiglinBossEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    public final AnimationState deathAnimationState = new AnimationState();

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return armorStacks;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot)
    {
        if(equipmentSlot.getType() ==  )
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
}
