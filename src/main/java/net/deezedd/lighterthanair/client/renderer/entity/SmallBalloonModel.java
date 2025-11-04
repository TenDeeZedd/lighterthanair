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

    // Textura pro "base" (koš), kterou jsi zmínil
    private static final ResourceLocation BASE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "textures/entity/small_balloon_base.png");

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

        // Pokud je to speciální varianta (např. Creeper), vrátíme jinou texturu
        // int variantId = entity.getVariantId();
        // if (variantId > 0) { ... }

        // Pro normální barvy
        return ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID,
                "textures/entity/small_" + color + "_balloon_envelope.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SmallBalloonEntity animatable) {
        return ANIMATION_RESOURCE;
    }

    // ===== BONUS: Zde je řešení pro "base" a "envelope" =====
    // Pokud chceš, aby "base" *vždy* používala jinou texturu než "envelope",
    // musíme použít GeckoLib Render Layers.
    // Prozatím ale předpokládáme, že "small_..._envelope.png" OBSAHUJE i texturu pro koš.

    // Pokud bys chtěl oddělené textury, museli bychom upravit RENDERER
    // a přepsat metodu `getTextureLocation` takto:
    /*
    @Override
    public ResourceLocation getTextureLocation(SmallBalloonEntity animatable) {
        // Renderer bude ve výchozím stavu používat texturu koše
        return BASE_TEXTURE;
    }
    */
    // A v Rendereru bychom přidali "layer", který překreslí jen "envelope" barevnou texturou.
    // Pro Fázi 1 je jednodušší mít texturu koše zkopírovanou na všech 16 souborech pláště.
}
