package net.sussyit.redpandamod.entity.client;

/* 3 second charge up, 1 second attack, circle shape */

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import org.joml.Vector3f;

import java.util.List;

public class ShieldSpinAttack implements IBossAttack{
    private int timer = 0;

    private final int WARNING_DURATION = 60; // 3 seconds of warning
    private final int ATTACK_DURATION = 20;  // Total time
    private final double RADIUS = 14.0;

    @Override
    public void start(PiglinBossEntity boss) {
        boss.attackShieldSpinAnimationState.start(boss.tickCount);
        boss.setAttackState(PiglinBossEntity.ATTACK_SHIELD_SPIN);
        this.timer = 0;
    }

    @Override
    public void tick(PiglinBossEntity boss) {
        timer++;

        // PHASE 1: CHARGING (Particles)
        if(timer < WARNING_DURATION) {
            if(timer % 2 == 0) {
                spawnAsteriskParticles(boss);
            }
        }

        // PHASE 2: THE HIT
        if (timer >= WARNING_DURATION && timer < (WARNING_DURATION + ATTACK_DURATION)) {
            detonateCircle(boss);
            // Spawn a massive amount of particles every tick of Phase 2
            spawnAuraParticles(boss);
        }
    }

    @Override
    public void stop(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.stop();
    }

    @Override
    public boolean isFinished() {
        return timer >= 80;
    }

    @Override
    public int getAnimationId() {
        return PiglinBossEntity.ATTACK_SHIELD_SPIN;
    }

    @Override
    public int getDuration() {
        return 80;
    }

    //Warning particles
    private void spawnAsteriskParticles(PiglinBossEntity boss) {
        if (boss.level().isClientSide()) return;
        ServerLevel level = (ServerLevel) boss.level();
        Vec3 center = boss.position();
        DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);

        int ringCount = 0;
        // We can use a larger r step (1.5) because we'll use spread to fill the gaps
        for (double r = 1.0; r <= RADIUS; r += 1.5) {
            ringCount++;

            double offsetAngle = (ringCount % 2 == 0) ? 0 : 10;

            // DYNAMIC STEP:
            // As r gets bigger, we need more particles (smaller stepSize)
            // to keep the visual density the same.
            // 40.0 / r is a good sweet spot for a radius of 14.
            double stepSize = Math.max(5, 40.0 / r);

            for (double i = 0; i < 360; i += stepSize) {
                double radians = Math.toRadians(i + offsetAngle);

                double x = center.x + Math.cos(radians) * r;
                double z = center.z + Math.sin(radians) * r;

                // Dithering to save FPS on a huge radius of 14
                if (boss.getRandom().nextFloat() < 0.2f) continue;

                // Increased spread to 0.4 to blur the "empty space"
                level.sendParticles(redDust, x, center.y + 0.1, z, 1, 0.4, 0, 0.4, 0);
            }
        }
    }

    //After effect particles
    private void spawnAuraParticles(PiglinBossEntity boss) {
        if (boss.level().isClientSide()) return;
        ServerLevel level = (ServerLevel) boss.level();

        // Increase the loop count (e.g., to 20 or 30) for much higher density
        for (int i = 0; i < 25; i++) {
            // Randomize location within the radius
            double offsetX = (boss.getRandom().nextDouble() - 0.5) * (RADIUS * 1.5);
            double offsetY = boss.getRandom().nextDouble() * 5.0; // Height of the effect
            double offsetZ = (boss.getRandom().nextDouble() - 0.5) * (RADIUS * 1.5);

            // Send a 'count' of 2 or 3 per call to further multiply density
            level.sendParticles(ParticleTypes.FLAME,
                    boss.getX() + offsetX, boss.getY() + offsetY, boss.getZ() + offsetZ,
                    3, 0.2, 0.2, 0.2, 0.05);

            // Add extra lava pops occasionally
            if (boss.getRandom().nextFloat() > 0.8f) {
                level.sendParticles(ParticleTypes.LAVA,
                        boss.getX() + offsetX, boss.getY() + offsetY, boss.getZ() + offsetZ,
                        1, 0, 0, 0, 0);
            }
        }
    }

    private void detonateCircle(PiglinBossEntity boss) {
        List<LivingEntity> targets = boss.level().getEntitiesOfClass(LivingEntity.class, boss.getBoundingBox().inflate(RADIUS), e -> e != boss); // finds all living targets in boss's bounding boxx and puts them in a list
        for (LivingEntity target : targets) { // goes through each entity in list
            if (boss.distanceTo(target) <= RADIUS) { // if mob inside circle, only checks those already in box
                target.hurt(boss.damageSources().mobAttack(boss), 15.0f);  // hurts player
                // Knockback away from boss
                double dx = target.getX() - boss.getX();
                double dz = target.getZ() - boss.getZ();
                target.knockback(3.5, -dx, -dz); // player knocked back
            }
        }
    }
}
