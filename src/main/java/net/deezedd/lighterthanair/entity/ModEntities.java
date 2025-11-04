package net.deezedd.lighterthanair.entity;

import net.deezedd.lighterthanair.LighterThanAir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LighterThanAir.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SmallBalloonEntity>> SMALL_BALLOON =
            ENTITY_TYPES.register("small_balloon",
                    () -> EntityType.Builder.<SmallBalloonEntity>of(
                                    (type, level) -> new SmallBalloonEntity(type, level),
                                    MobCategory.MISC
                            )
                            .sized(3.0f, 3.0f) // Šířka 3 bloky, výška 3 bloky (můžeme později doladit)
                            .clientTrackingRange(10) // Sledování na dálku
                            .build("small_balloon"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
