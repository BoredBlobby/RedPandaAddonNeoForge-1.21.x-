package net.sussyit.redpandamod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CameraShakeUtils {
    private static float shakeIntensity = 0f;
    private static int shakeDuration = 0;
    private static boolean shouldFreeze = false;
    private static float fovOffset = 0f;
    private static int fovDuration = 0;
    private static float targetFovLimit = 0.15f; // How much "extra" FOV to add

    // Call this from handleEntityEvent
    public static void shake(int duration, float intensity, boolean freeze, float fovLimit) {
        shakeDuration = duration;
        shakeIntensity = intensity;
        shouldFreeze = freeze;
        fovDuration = duration;
        targetFovLimit = fovLimit; //how "far out" the zoom goes
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


    public static void forcePlayerLookAt(Player player, Entity target) {
        if (player == null || target == null) return;

        Vec3 playerEyes = player.getEyePosition();
        Vec3 targetPos = target.getEyePosition();

        double dX = targetPos.x - playerEyes.x;
        double dY = targetPos.y - playerEyes.y;
        double dZ = targetPos.z - playerEyes.z;
        double dXZ = Math.sqrt(dX * dX + dZ * dZ);

        // Calculate Yaw and Pitch in degrees
        float yaw = (float) (Math.atan2(dZ, dX) * (180 / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(dY, dXZ) * (180 / Math.PI)));

        // Apply rotations to the player
        player.setYRot(yaw);
        player.setXRot(pitch);

        // Crucial: Update the "Old" rotations to prevent the camera from snapping back
        player.yRotO = yaw;
        player.xRotO = pitch;
    }
}
