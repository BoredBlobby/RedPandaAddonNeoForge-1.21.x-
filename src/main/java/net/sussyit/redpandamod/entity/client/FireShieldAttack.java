package net.sussyit.redpandamod.entity.client;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;
import net.sussyit.redpandamod.util.CameraShakeUtils;
import org.joml.Vector3f;

import java.util.List;

public class FireShieldAttack implements IBossAttack{
    private int timer = 0;
    private double targetAngle = 0; // The "North" of our asterisk

    private final int WARNING_DURATION = 120; // 2 seconds of warning
    private final int ATTACK_DURATION = 20;  // Total time
    private final int RAY_LENGTH = 30;       // How far the lines go
    private final int RAY_COUNT = 8;         // 8 Rays = Asterisk shape (*)


    @Override
    public void start(PiglinBossEntity boss) {
        boss.attackFireShieldAnimationState.start(boss.tickCount);
        boss.setAttackState(PiglinBossEntity.ATTACK_FIRE_SHIELD);

        this.timer = 0;
        // --- TARGETING LOGIC ---
        // Find the nearest player within 16 blocks to aim at
        Player target = boss.level().getNearestPlayer(boss, 30.00D);
        if (target != null) {
            double dx = target.getX() - boss.getX();
            double dz = target.getZ() - boss.getZ();
            // Calculate the base angle toward the player
            this.targetAngle = Math.toDegrees(Math.atan2(dz, dx));
        } else {
            this.targetAngle = 0; // Default if no player found
        }
    }

    @Override
    public void tick(PiglinBossEntity boss) {
        timer++;

        // PHASE 1: TELEGRAPH (Warning Lines)
        if (timer < WARNING_DURATION && timer % 10 == 0) {
            spawnAsteriskParticles(boss, 1f); // Weak smoke
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
        if (boss.level().isClientSide()) return;

        ServerLevel level = (ServerLevel) boss.level();
        Vec3 center = boss.position();
        double stepSize = 3;

        // How far to the left and right the "extra" lines should be
        double beamWidthOffset = 0.6;

        double angleStep = 360.0 / RAY_COUNT;

        for (int i = 0; i < RAY_COUNT; i++) {
            double angleRad = Math.toRadians(i * angleStep + targetAngle);

            // Forward vector
            double dirX = Math.cos(angleRad);
            double dirZ = Math.sin(angleRad);

            // PERPENDICULAR (SIDEWAYS) VECTOR
            // Rotating the forward vector 90 degrees gives us the "side" direction
            double sideX = -dirZ;
            double sideZ = dirX;

            for (double r = 1; r <= RAY_LENGTH; r += stepSize) {
                double centerX = center.x + (dirX * r);
                double centerZ = center.z + (dirZ * r);
                double y = center.y + 0.2;

                // 1. Center line
                level.sendParticles(particle, centerX, y, centerZ, 1, spread, 0.0, spread, 0.4);

                // 2. Left line (offset by sideX/sideZ)
                level.sendParticles(particle,
                        centerX + (sideX * beamWidthOffset), y,
                        centerZ + (sideZ * beamWidthOffset), 1, spread, 0.0, spread, 0.4);

                // 3. Right line (offset by negative sideX/sideZ)
                level.sendParticles(particle,
                        centerX - (sideX * beamWidthOffset), y,
                        centerZ - (sideZ * beamWidthOffset), 1, spread, 0.0, spread, 0.4);
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
        DustParticleOptions redDust = new DustParticleOptions(new Vector3f(1.0f, 0.0f, 0.0f), 2.5f);

        double angleStep = 360.0 / RAY_COUNT;

        for (int i = 0; i < RAY_COUNT; i++) {
            double angleRad = Math.toRadians(i * angleStep + targetAngle);
            double dirX = Math.cos(angleRad);
            double dirZ = Math.sin(angleRad);

            // Calculate a 'perpendicular' vector to create width
            double perpX = -dirZ;
            double perpZ = dirX;

            for (double r = 1; r <= RAY_LENGTH; r += 1.2) {
                double x = center.x + (dirX * r);
                double y = center.y + 0.2;
                double z = center.z + (dirZ * r);
                for (float widthOffset = -1.0f; widthOffset <= 1.0f; widthOffset += 0.5f) {
                    double px = x + (perpX * widthOffset);
                    double pz = z + (perpZ * widthOffset);

                    // Use 'redDust' here instead of ParticleTypes.SMOKE
                    level.sendParticles(redDust, px, y, pz, 1, 0.2, 0, 0.2, 0.0);
                }
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
    private boolean isInStarShape(Vec3 origin, Vec3 target, double halfWidth) {
        double dx = target.x - origin.x;
        double dz = target.z - origin.z;
        double distSq = dx * dx + dz * dz;

        if (distSq > RAY_LENGTH * RAY_LENGTH || distSq < 0.5) return false;

        double angleStep = 360.0 / RAY_COUNT;

        for (int i = 0; i < RAY_COUNT; i++) {
            double rayAngleRad = Math.toRadians(i * angleStep + targetAngle);

            // This is a vector representing the direction of the ray
            double rayX = Math.cos(rayAngleRad);
            double rayZ = Math.sin(rayAngleRad);

            // Project the player's position onto the ray line
            // This tells us how far along the ray the player is
            double projection = dx * rayX + dz * rayZ;

            if (projection > 0 && projection <= RAY_LENGTH) {
                // This calculates the perpendicular distance from the player to the ray
                double closestX = origin.x + rayX * projection;
                double closestZ = origin.z + rayZ * projection;

                double distanceToLine = Math.sqrt(Math.pow(target.x - closestX, 2) + Math.pow(target.z - closestZ, 2));

                if (distanceToLine <= halfWidth) {
                    return true;
                }
            }
        }
        return false;
    }
}
