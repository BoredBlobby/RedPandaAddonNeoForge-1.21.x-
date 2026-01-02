package net.sussyit.redpandamod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class CameraShakeUtils {
    private static float shakeIntensity = 0f;
    private static int shakeDuration = 0;

    // Call this from handleEntityEvent
    public static void shake(int duration, float intensity) {
        shakeDuration = duration;
        shakeIntensity = intensity;
    }

    // This needs to be called every frame (RenderGuiEvent or similar)
    // or you can use a simpler "One-off" method below
    public static void applyShake(Minecraft mc) {
        if (shakeDuration > 0 && mc.player != null) {
            float randomX = (mc.player.getRandom().nextFloat() - 0.5f) * shakeIntensity;
            float randomY = (mc.player.getRandom().nextFloat() - 0.5f) * shakeIntensity;

            mc.player.setXRot(mc.player.getXRot() + randomX);
            mc.player.setYRot(mc.player.getYRot() + randomY);

            shakeDuration--;
        }
    }
}
