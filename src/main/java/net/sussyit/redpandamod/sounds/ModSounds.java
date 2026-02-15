package net.sussyit.redpandamod.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sussyit.redpandamod.RedPandaMod;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, RedPandaMod.MODID);

    public static final Supplier<SoundEvent> GFHURT = registerSoundEvent("gfhurt");

    public static final Supplier<SoundEvent> GFBOBA = registerSoundEvent("gfboba");
    public static final Supplier<SoundEvent> GFACCEPT = registerSoundEvent("gfaccept");
    public static final Supplier<SoundEvent> GFREJECT = registerSoundEvent("gfreject");


    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RedPandaMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
