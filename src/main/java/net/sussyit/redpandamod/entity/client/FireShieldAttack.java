package net.sussyit.redpandamod.entity.client;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.util.CameraShakeUtils;
import org.joml.Vector3f;

import java.util.List;

public class FireShieldAttack implements IBossAttack{
    private int timer = 0;

    private final int WARNING_DURATION = 120; // 2 seconds of warning
    private final int ATTACK_DURATION = 20;  // Total time
    private final int RAY_LENGTH = 12;       // How far the lines go
    private final int RAY_COUNT = 8;         // 8 Rays = Asterisk shape (*)


    @Override
    public void start(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.start(boss.tickCount);
        boss.setAttackState(PiglinBossEntity.ATTACK_FIRE_SHIELD);
        this.timer = 0;
    }

    @Override
    public void tick(PiglinBossEntity boss) {
        timer++;

        // PHASE 1: TELEGRAPH (Warning Lines)
        if (timer < WARNING_DURATION) {
            spawnAsteriskParticles(boss, 0.5f); // Weak smoke
        }

        // PHASE 2: DETONATION (The Hit)
        if (timer == WARNING_DURATION) {
            // 1. Visuals: Big Flame Particles
            spawnAsteriskParticles(boss, ParticleTypes.FLAME, 0.1f);
            spawnAsteriskParticles(boss, ParticleTypes.LAVA, 0.5f);

            // 2. Logic: Damage Logic
            detonateAsterisk(boss);

            // 3. Effects
            CameraShakeUtils.shake(20, 1.5f, false);
            // boss.playSound(SoundEvents.GENERIC_EXPLODE, 1.0f, 1.0f);
        }
    }

    @Override
    public void stop(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.stop();
    }


    @Override
    public boolean isFinished() {
        return timer >= 140;
    }

    @Override
    public int getAnimationId() {
        return PiglinBossEntity.ATTACK_FIRE_SHIELD;
    }

    @Override
    public int getDuration() {
        return 140;
    }

    // --- HELPER METHODS ---

    /**
     * Spawns particles along the lines of the asterisk.
     */
    private void spawnAsteriskParticles(PiglinBossEntity boss, net.minecraft.core.particles.SimpleParticleType particle, float spread) {
        if (boss.level().isClientSide()) return; // Particles are usually handled via packets or ServerLevel

        ServerLevel level = (ServerLevel) boss.level();
        Vec3 center = boss.position();
        double stepSize = 0.5; // Spawn a particle every half block

        // 360 degrees divided by 8 rays = 45 degree intervals
        double angleStep = 360.0 / RAY_COUNT;

        for (int i = 0; i < RAY_COUNT; i++) {
            double angleRad = Math.toRadians(i * angleStep); // Convert 0, 45, 90... to radians

            // Calculate direction vector for this ray
            double dirX = Math.cos(angleRad);
            double dirZ = Math.sin(angleRad);

            // Draw the line
            for (double r = 1; r <= RAY_LENGTH; r += stepSize) {
                double x = center.x + (dirX * r);
                double y = center.y + 0.2; // Slightly above ground
                double z = center.z + (dirZ * r);

                // Spawn the particle
                level.sendParticles(particle, x, y, z, 1, spread, 0.0, spread, 0.0);
            }
        }
    }

    /* SPAWNS ONLY RED PARTICLES */
    private void spawnAsteriskParticles(PiglinBossEntity boss, float spread) {
        if (boss.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) boss.level();
        Vec3 center = boss.position();

        // Define Red Dust: (Red, Green, Blue, Scale)
        // 1.0f, 0.0f, 0.0f makes it pure Red.
        DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 1.5f);

        double angleStep = 360.0 / RAY_COUNT;

        for (int i = 0; i < RAY_COUNT; i++) {
            double angleRad = Math.toRadians(i * angleStep);
            double dirX = Math.cos(angleRad);
            double dirZ = Math.sin(angleRad);

            for (double r = 1; r <= RAY_LENGTH; r += 0.5) {
                double x = center.x + (dirX * r);
                double y = center.y + 0.2;
                double z = center.z + (dirZ * r);

                // Use 'redDust' here instead of ParticleTypes.SMOKE
                level.sendParticles(redDust, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    /**
     * Checks for entities along the lines and hurts them.
     */
    private void detonateAsterisk(PiglinBossEntity boss) {
        if (boss.level().isClientSide()) return;

        Vec3 center = boss.position();
        double angleStep = 360.0 / RAY_COUNT;
        double hitboxWidth = 1.5; // How wide the "fire" is

        // We scan for players in the entire area first to save performance
        AABB giantBox = boss.getBoundingBox().inflate(RAY_LENGTH);
        List<LivingEntity> potentialTargets = boss.level().getEntitiesOfClass(LivingEntity.class, giantBox, e -> e != boss && e.isAlive());

        for (LivingEntity target : potentialTargets) {
            if (isInStarShape(center, target.position(), hitboxWidth)) {
                target.hurt(boss.damageSources().mobAttack(boss), 12.0f);
                target.setRemainingFireTicks(60); // Burn them!
            }
        }
    }

    /**
     * Math Helper: Checks if a target point is inside one of the asterisk rays.
     */
    private boolean isInStarShape(Vec3 origin, Vec3 target, double width) {
        // 1. Check distance first (Circle check)
        double distSq = origin.distanceToSqr(target);
        if (distSq > RAY_LENGTH * RAY_LENGTH) return false;

        // 2. Check angles
        double dx = target.x - origin.x;
        double dz = target.z - origin.z;

        // Calculate the angle from boss to target (in degrees)
        double angleToTarget = Math.toDegrees(Math.atan2(dz, dx));
        if (angleToTarget < 0) angleToTarget += 360;

        double angleStep = 360.0 / RAY_COUNT; // 45 degrees

        // Check if this angle matches one of our 8 rays (within tolerance)
        // We accept if the angle is close to 0, 45, 90, etc.
        double tolerance = 15.0; // How "wide" the cone of damage is (in degrees)

        for (int i = 0; i < RAY_COUNT; i++) {
            double rayAngle = i * angleStep;
            double diff = Math.abs(angleToTarget - rayAngle);

            // Handle the 360/0 wrap-around edge case
            if (diff > 180) diff = 360 - diff;

            if (diff < tolerance) {
                return true; // Hit!
            }
        }
        return false;
    }
}
