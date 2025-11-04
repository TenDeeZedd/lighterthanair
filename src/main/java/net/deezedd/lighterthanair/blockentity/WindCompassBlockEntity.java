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
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WindCompassBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        boolean isCurrentlyFloating = state.getValue(WindCompassBlock.FLOATING);

        // Item Frame search
        AABB blockAABB = new AABB(pos);
        List<ItemFrame> itemFrames = level.getEntitiesOfClass(ItemFrame.class, blockAABB);
        boolean itemFramePresent = false;
        for (ItemFrame frame : itemFrames) {
            if (frame.getXRot() == -90.0f) {
                itemFramePresent = true;
                break;
            }
        }

        if (itemFramePresent && !isCurrentlyFloating) {
            level.setBlock(pos, state.setValue(WindCompassBlock.FLOATING, true), 3);
        }

        if (!itemFramePresent && isCurrentlyFloating) {
            level.setBlock(pos, state.setValue(WindCompassBlock.FLOATING, false), 3);
        }
    }
}
