package net.sussyit.redpandamod.entity.client;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.util.CameraShakeUtils;

import java.util.List;

public class EarthquakeAttack implements IBossAttack{
    private int timer = 0;

    private final int CHARGE_DURATION = 60;
    private final int STOMP_DURATION = 20;
    private final FissureManager earthquakeAttack = new FissureManager();

    @Override
    public void start(PiglinBossEntity boss) {
        this.timer = 0;
        boss.setAttackState(PiglinBossEntity.ATTACK_EARTHQUAKE);
        boss.attackEarthQuakeAnimationState.start(boss.tickCount);

        boss.playSound(SoundEvents.PIGLIN_ANGRY, 1.0f, 0.5f);
    }

    @Override
    public void tick(PiglinBossEntity boss) {
        timer++;

        // Phase 1: Charging Shake
        if (timer < CHARGE_DURATION) {
            CameraShakeUtils.shake(5, 0.5f, false, 0.25f); // Light rumble
            // Particles gathering at feet?
        }

        // Phase 2: THE STOMP (Tick 60)
        if (timer == CHARGE_DURATION) {

            // 2. Big Shake


            // 3. TRIGGER THE BACKGROUND LOGIC
            // This hands off the lava logic to the Entity class
            System.out.println("earthquake Attack is getting called");
            boss.triggerEarthquake();

            // 4. Knockback players nearby
            AABB shockwave = boss.getBoundingBox().inflate(10.0);
            List<LivingEntity> targets = boss.level().getEntitiesOfClass(LivingEntity.class, shockwave, e -> e != boss);
            for(LivingEntity target : targets) {
                if (target.onGround()) {
                    target.knockback(2.0, boss.getX() - target.getX(), boss.getZ() - target.getZ());
                }
            }
        }
    }

    @Override
    public void stop(PiglinBossEntity boss) {
        boss.attackEarthQuakeAnimationState.stop();
    }

    @Override
    public boolean isFinished() {
        return timer >= 80;
    }

    @Override
    public int getAnimationId() {
        return PiglinBossEntity.ATTACK_EARTHQUAKE;
    }

    @Override
    public int getDuration() {
        return 80;
    }
}
