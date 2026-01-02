package net.sussyit.redpandamod.event;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.sussyit.redpandamod.RedPandaMod;
import net.sussyit.redpandamod.util.CameraShakeUtils;

// Note: Bus.GAME is the default, so we don't need to specify it.
// value = Dist.CLIENT ensures this code never runs on a dedicated server (which would crash).
@EventBusSubscriber(modid = RedPandaMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // This runs every tick on the client side
        CameraShakeUtils.applyShake(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (CameraShakeUtils.isPlayerFrozen()) {
            // This clears all keyboard input (WASD, Jump, Sneak)
            event.getInput().forwardImpulse = 0;
            event.getInput().leftImpulse = 0;
            event.getInput().up = false;
            event.getInput().down = false;
            event.getInput().left = false;
            event.getInput().right = false;
            event.getInput().jumping = false;
            event.getInput().shiftKeyDown = false;
        }
    }
}
