package net.deezedd.lighterthanair.blockentity;

import net.deezedd.lighterthanair.block.WindCompassBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class WindCompassBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WindCompassBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WIND_COMPASS_BE.get(), pPos, pBlockState);
        // Nepotřebujeme SingletonGeoAnimatable, protože nic netriggerujeme
    }

    // Controller jen běží, aby renderer mohl manipulovat s kostmi
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WindCompassBlockEntity blockEntity) {
        if (level.isClientSide()) return; // Logika jen na serveru

        boolean isCurrentlyFloating = state.getValue(WindCompassBlock.FLOATING);

        // Hledáme Item Frame
        AABB blockAABB = new AABB(pos);
        List<ItemFrame> itemFrames = level.getEntitiesOfClass(ItemFrame.class, blockAABB);
        boolean itemFramePresent = false;
        for (ItemFrame frame : itemFrames) {
            if (frame.getXRot() == -90.0f) {
                itemFramePresent = true;
                break;
            }
        }

        // Případ 1: Item Frame tam je, ale blok není floating (Mělo by být řešeno v getStateForPlacement, ale pojistka)
        if (itemFramePresent && !isCurrentlyFloating) {
            level.setBlock(pos, state.setValue(WindCompassBlock.FLOATING, true), 3);
        }

        // Případ 2: Item Frame tam NENÍ, ale blok je floating (Hráč zničil Item Frame)
        if (!itemFramePresent && isCurrentlyFloating) {
            level.setBlock(pos, state.setValue(WindCompassBlock.FLOATING, false), 3); // Spadne dolů
        }
    }
}
