package net.deezedd.lighterthanair.sound;

import net.deezedd.lighterthanair.LighterThanAir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, LighterThanAir.MODID);


    // Weather Vane Creaks + List of Creaks
    public static final Supplier<SoundEvent> VANE_CREAK_1 = registerSoundEvent("vane_creak1");
    public static final Supplier<SoundEvent> VANE_CREAK_2 = registerSoundEvent("vane_creak2");
    public static final Supplier<SoundEvent> VANE_CREAK_3 = registerSoundEvent("vane_creak3");
    public static final Supplier<SoundEvent> VANE_CREAK_4 = registerSoundEvent("vane_creak4");
    public static final Supplier<SoundEvent> VANE_CREAK_5 = registerSoundEvent("vane_creak5");
    public static final List<Supplier<SoundEvent>> VANE_CREAKS = List.of(
            VANE_CREAK_1, VANE_CREAK_2, VANE_CREAK_3, VANE_CREAK_4, VANE_CREAK_5
    );

    public static final Supplier<SoundEvent> CRATE_POP = registerSoundEvent("crate_pop");


    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));

    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
