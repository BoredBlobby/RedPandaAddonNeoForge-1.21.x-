package net.sussyit.redpandamod.entity.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.util.CameraShakeUtils;

import java.util.List;

public class FireShieldAttack implements IBossAttack{
    private int timer = 0;


    @Override
    public void start(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.start(boss.tickCount);
        boss.setAttackState(PiglinBossEntity.ATTACK_FIRE_SHIELD);
        this.timer = 0;
    }

    @Override
    public void tick(PiglinBossEntity boss) {
        timer++;

        if(timer == 120) {
            if (!boss.level().isClientSide()) { // CRITICAL: Only deal damage on Server side!

                // 1. Define the range (5 blocks radius)
                double range = 5.0D;
                AABB area = boss.getBoundingBox().inflate(range);  // inflates 5 blocks around the character

                // 2. Get the entities
                // Param 1: Look for LivingEntity.class
                // Param 2: The box we defined above
                // Param 3: A "Predicate" to ignore the boss itself (so it doesn't hurt itself)
                List<LivingEntity> targets = boss.level().getEntitiesOfClass(LivingEntity.class, area,
                        entity -> entity != boss && entity.isAlive());

                // 3. Loop and hurt
                for(LivingEntity target : targets) {
                    target.hurt(boss.damageSources().mobAttack(boss), 10.0f);
                }
            }

            // Logic to shake screen (Client side visual, can be outside the check or handled via event)
            CameraShakeUtils.shake(20, 2f, false);
        }
    }

    @Override
    public void stop(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.stop();
    }


    @Override
    public boolean isFinished() {
        return timer >= 120;
    }

    @Override
    public int getAnimationId() {
        return PiglinBossEntity.ATTACK_FIRE_SHIELD;
    }

    @Override
    public int getDuration() {
        return 120;
    }
}
