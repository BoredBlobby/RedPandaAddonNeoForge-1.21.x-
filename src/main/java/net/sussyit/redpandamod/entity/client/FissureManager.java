package net.sussyit.redpandamod.entity.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FissureManager {

    //Internal class to track individual lava spots
    private static class Fissure {
        BlockPos pos; //the position of the lava spot
        int delayTimer; //The delay timer before actually placing (0-8 secs)
        int fuseTimer; // The time before it spreads(10 secs)
        boolean isActive; // Magmma becomes visible

        public Fissure(BlockPos pos, int delay) {
            this.pos = pos;
            this.delayTimer = delay;
            this.fuseTimer = 400; // 10 secs
            this.isActive = false;
        }
    }

    private final List<Fissure> activeFissures = new ArrayList<>();
    private final Random random = new Random();

    //Call this once when the stop happens
    public void startEarthquake(PiglinBossEntity boss, int count, int radius) {
        if (boss.level().isClientSide()) return;

        double minRadius = 4.0; // prevents from spawning lava underneath the boss

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = minRadius + (random.nextDouble() * (radius - minRadius));
            int x = (int) (boss.getX() + Math.cos(angle) * dist);
            int z = (int) (boss.getZ() + Math.sin(angle) * dist);

            // Find ground logic
            BlockPos targetPos = new BlockPos(x, (int) boss.getY() + 2, z);
            Level level = boss.level();
            int safety = 20;
            while (level.isEmptyBlock(targetPos) && safety > 0) {
                targetPos = targetPos.below();
                safety--;
            }

            if (level.getBlockState(targetPos).isSolid()) {
                activeFissures.add(new Fissure(targetPos, random.nextInt(160)));
            }
        }
    }

    public void tick(Level level) {
        if (level.isClientSide() || activeFissures.isEmpty()) return;

        Iterator<Fissure> iterator = activeFissures.iterator();
        while (iterator.hasNext()) {
            Fissure f = iterator.next();

            // --- PHASE 1: SPAWN THE LAVA "PUDDLE" ---
            if (!f.isActive) {
                if (f.delayTimer > 0) f.delayTimer--;

                if (f.delayTimer <= 0) {
                    // Turn the floor block into LAVA
                    // Since it is in a 1-block hole, it won't spread yet.
                    level.setBlock(f.pos, Blocks.LAVA.defaultBlockState(), 3);
                    level.levelEvent(2001, f.pos, Block.getId(Blocks.LAVA.defaultBlockState()));
                    f.isActive = true;
                }
            }
            // --- PHASE 2: WAIT FOR ERUPTION ---
            else {
                // Visuals: Smoke rising from the lava to warn the player
                if (random.nextInt(10) == 0) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE,
                            f.pos.getX() + 0.5, f.pos.getY() + 1.0, f.pos.getZ() + 0.5,
                            1, 0, 0.2, 0, 0.05);
                }

                f.fuseTimer--;

                // --- TIME IS UP ---
                if (f.fuseTimer <= 0) {
                    // CHECK: Is the block still lava?
                    // If the player placed a block (Cobblestone/Dirt) here, it won't be lava anymore.
                    if (level.getBlockState(f.pos).is(Blocks.LAVA)) {

                        // Player failed to plug it! ERUPTION!
                        // We place a SECOND lava block ON TOP of the floor.
                        // This one has no walls, so it will start flowing immediately.
                        BlockPos above = f.pos.above();
                        if (level.isEmptyBlock(above)) {
                            level.setBlock(above, Blocks.LAVA.defaultBlockState(), 3);
                            level.playSound(null, f.pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.HOSTILE, 1.0f, 1.0f);
                        }
                    } else {
                        // Player successfully plugged the hole!
                        level.playSound(null, f.pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.HOSTILE, 0.5f, 1.0f);
                    }

                    iterator.remove(); // Stop tracking this fissure
                }
            }
        }
    }
}
