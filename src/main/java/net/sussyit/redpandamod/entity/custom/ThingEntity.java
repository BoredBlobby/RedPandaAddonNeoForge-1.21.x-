package net.sussyit.redpandamod.entity.custom;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sussyit.redpandamod.util.CameraShakeUtils;

public class ThingEntity extends LivingEntity {

    private final NonNullList<ItemStack> handStacks = NonNullList.withSize(2, ItemStack.EMPTY); // Mainhand and offhand
    private final NonNullList<ItemStack> armorStacks = NonNullList.withSize(4, ItemStack.EMPTY); // 4 pieces of armor
    private final CameraShakeUtils cameraShakeUtils = new CameraShakeUtils();



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
}
