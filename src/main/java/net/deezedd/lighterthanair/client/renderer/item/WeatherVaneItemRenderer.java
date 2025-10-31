package net.deezedd.lighterthanair.client.renderer.item;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.custom.WeatherVaneBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

class WeatherVaneItemModel extends GeoModel<WeatherVaneBlockItem> {
    private static final ResourceLocation MODEL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "geo/weather_vane.geo.json");
    private static final ResourceLocation TEXTURE_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/block/weather_vane.png");
    private static final ResourceLocation ANIMATION_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "animations/weather_vane.animation.json"); // Tento soubor musí existovat!

    @Override
    public ResourceLocation getModelResource(WeatherVaneBlockItem animatable) {
        return MODEL_RESOURCE;
    }

    @Override
    public ResourceLocation getTextureResource(WeatherVaneBlockItem animatable) {
        return TEXTURE_RESOURCE;
    }

    @Override
    public ResourceLocation getAnimationResource(WeatherVaneBlockItem animatable) {
        return ANIMATION_RESOURCE;
    }

    @Override
    public void setCustomAnimations(WeatherVaneBlockItem animatable, long instanceId, AnimationState<WeatherVaneBlockItem> animationState) {
        // Forced stop rotation of item model
        GeoBone rootBone = this.getAnimationProcessor().getBone("vane_rotate");

        if (rootBone != null) {
            rootBone.setRotX(0);
            rootBone.setRotY(0);
            rootBone.setRotZ(0);
        }
    }
}

// A samotný renderer
public class WeatherVaneItemRenderer extends GeoItemRenderer<WeatherVaneBlockItem> {
    public WeatherVaneItemRenderer() {
        super(new WeatherVaneItemModel());
    }

}
