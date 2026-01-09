package net.sussyit.redpandamod.entity.client;

import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

public class ShadowVoidAttack implements IBossAttack{
    private int timer = 0;
    private double targetAngle = 0; // The "North" of our asterisk

    private final int WARNING_DURATION = 40; // 2 seconds of warning
    private final int ATTACK_DURATION = 10;  // Total time


    @Override
    public void start(PiglinBossEntity boss) {

    }

    @Override
    public void tick(PiglinBossEntity boss) {

    }

    @Override
    public void stop(PiglinBossEntity boss) {

    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public int getAnimationId() {
        return 0;
    }

    @Override
    public int getDuration() {
        return 0;
    }
}
