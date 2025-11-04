package net.deezedd.lighterthanair.client.renderer.entity;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.entity.SmallBalloonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SmallBalloonModel extends GeoModel<SmallBalloonEntity> {

    // Cesty k souborům (budeme předpokládat, že se takto jmenují)
    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "geo/small_hot_air_balloon.geo.json");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "animations/small_hot_air_balloon.animation.json");

    @Override
    public ResourceLocation getModelResource(SmallBalloonEntity animatable) {
        return MODEL_RESOURCE;
    }

    /**
     * Toto je klíčová metoda pro dynamické textury.
     * Vrátí správnou texturu "envelope" na základě barvy entity.
     */
    @Override
    public ResourceLocation getTextureResource(SmallBalloonEntity entity) {
        String color = entity.getColor();

        // Pro normální barvy
        return ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID,
                "textures/entity/small_" + color + "_balloon_envelope.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallBalloonEntity animatable) {
        return ANIMATION_RESOURCE;
    }

}
