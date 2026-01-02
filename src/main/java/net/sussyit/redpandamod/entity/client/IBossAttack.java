package net.sussyit.redpandamod.entity.client;


import net.sussyit.redpandamod.entity.custom.PiglinBossEntity;

public interface IBossAttack {
    void start(PiglinBossEntity boss);
    void tick(PiglinBossEntity boss);
    void stop(PiglinBossEntity boss);
    boolean isFinished();
    int getAnimationId(); // Links to your CURRENT_ATTACK ID
    int getDuration(); // How long the attack lasts
}
