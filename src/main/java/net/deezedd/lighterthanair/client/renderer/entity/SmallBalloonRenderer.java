package net.deezedd.lighterthanair.client.renderer.entity;

import net.deezedd.lighterthanair.entity.SmallBalloonEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SmallBalloonRenderer extends GeoEntityRenderer<SmallBalloonEntity> {

    public SmallBalloonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SmallBalloonModel());

        // Stín entity (velikost)
        this.shadowRadius = 0.8f;
    }

    // Prozatím je renderer jednoduchý.
    // Model (`BalloonModel.java`) se stará o výběr správné textury.
}
