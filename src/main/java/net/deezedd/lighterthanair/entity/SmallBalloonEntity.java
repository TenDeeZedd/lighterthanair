package net.deezedd.lighterthanair.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class SmallBalloonEntity extends AbstractBalloonEntity implements GeoAnimatable{

    /**
     * OPRAVA ZDE:
     * Konstruktor pro továrnu (factory) musí být specifický pro EntityType<SmallBalloonEntity>,
     * aby kompilátor správně odvodil typ v ModEntities.java.
     */
    // Tento konstruktor používáme pro manuální spawn (např. z bedny)
    public SmallBalloonEntity(EntityType<SmallBalloonEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // Tento konstruktor používáme pro manuální spawn (např. z bedny)
    public SmallBalloonEntity(Level pLevel, double pX, double pY, double pZ) {
        super(ModEntities.SMALL_BALLOON.get(), pLevel);
        this.setPos(pX, pY, pZ);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
