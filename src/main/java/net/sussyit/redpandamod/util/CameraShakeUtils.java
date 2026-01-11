package net.sussyit.redpandamod.util;

import net.minecraft.client.Minecraft;
public class CameraShakeUtils {
    private static float shakeIntensity = 0f;
    private static int shakeDuration = 0;
    private static boolean shouldFreeze = false;
    private static float fovOffset = 0f;
    private static int fovDuration = 0;
    private static float targetFovLimit = 0.15f; // How much "extra" FOV to add

    // Call this from handleEntityEvent
    public static void shake(int duration, float intensity, boolean freeze) {
        shakeDuration = duration;
        shakeIntensity = intensity;
        shouldFreeze = freeze;
        fovDuration = duration;
        targetFovLimit = 0.25f; //how "far out" the zoom goes
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

            if(shakeDuration <= 0) {
                shouldFreeze = false;
            }
        }
    }

    public static boolean isPlayerFrozen() {
        //System.out.println("Current Player: " + shouldFreeze);
        return shouldFreeze;
    }

    public static float getFovModifier() {
        return fovOffset;
    }

    // Call this in a Client Tick event to smoothly transition
    public static void tickFov() {
        if (fovDuration > 0) {
            // Smoothly move toward the target intensity
            fovOffset = Math.min(fovOffset + 0.02f, targetFovLimit);
            fovDuration--;
        } else {
            // Smoothly return to 0
            fovOffset = Math.max(fovOffset - 0.02f, 0f);
        }
    }
}
