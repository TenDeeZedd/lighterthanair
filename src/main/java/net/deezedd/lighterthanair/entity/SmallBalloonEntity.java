package net.deezedd.lighterthanair.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB; // <-- Důležité pro getPickBoundingBox
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

public class SmallBalloonEntity extends AbstractBalloonEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

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
        return this.cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }

    private static final VoxelShape BASKET_SHAPE = Shapes.box(
            -0.5, // 0.5 bloku doleva od středu
            0.0,  // Od nohou
            -0.5, // 0.5 bloku dozadu od středu
            0.5,  // 0.5 bloku doprava od středu
            1.0,  // 1 blok vysoko
            0.5   // 0.5 bloku dopředu od středu
    );

    private static final VoxelShape ENVELOPE_SHAPE = Shapes.box(
            -1.5, // 1.5 bloku doleva
            3.0,  // Začíná 2 bloky nad nohama (1 blok mezera nad košem)
            -1.5, // 1.5 bloku dozadu
            1.5,  // 1.5 bloku doprava
            6.0,  // Končí 5 bloků nad nohama (3 bloky výška)
            1.5   // 1.5 bloku dopředu
    );

    private static final VoxelShape BURNER_SHAPE = Shapes.box(
            -0.25, // 1.5 bloku doleva
            2.0,  // Začíná 2 bloky nad nohama (1 blok mezera nad košem)
            -0.25, // 1.5 bloku dozadu
            0.25,  // 1.5 bloku doprava
            2.5,  // Končí 5 bloků nad nohama (3 bloky výška)
            0.25   // 1.5 bloku dopředu
    );

    private static final VoxelShape COMBINED_SHAPE = Shapes.or(
            BASKET_SHAPE,
            ENVELOPE_SHAPE,
            BURNER_SHAPE
    );


    /**
     * Toto je správná metoda pro definování vlastního kolizního tvaru entity.
     * Používá se pro kolize s hráčem a ostatními entitami.
     * @param pEntity Entita, která se snaží interagovat (kolidovat).
     */
    @Override
    public VoxelShape getInteractionShape(Entity pEntity) {
        return COMBINED_SHAPE;
    }

    /**
     * Toto překrýváme, aby se zajistilo, že náš vlastní VoxelShape
     * je použit i pro obecné kolize se světem (např. aby se entita netlačila do zdi).
     */
    @Override
    public VoxelShape getCollisionShapeForBlockMovement() {
        return COMBINED_SHAPE;
    }

    /**
     * Volitelné: Můžeme definovat "pick" box (pro míření/výběr) zvlášť.
     * Pokud chceme, aby odpovídal našim vlastním tvarům, uděláme to takto.
     * Pokud ne, smaž tuto metodu a použije se výchozí box z ModEntities.
     */
    @Override
    public AABB getPickBoundingBox() {
        // .bounds() převede VoxelShape na AABB
        // .move(this.position()) posune AABB na aktuální pozici entity ve světě
        return COMBINED_SHAPE.bounds().move(this.position());
    }

}
