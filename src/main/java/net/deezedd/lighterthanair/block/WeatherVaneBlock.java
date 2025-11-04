package net.deezedd.lighterthanair.block;

import com.mojang.serialization.MapCodec;
import net.deezedd.lighterthanair.blockentity.ModBlockEntities;
import net.deezedd.lighterthanair.blockentity.WeatherVaneBlockEntity;
import net.deezedd.lighterthanair.util.ModGameRules;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WeatherVaneBlock extends BaseEntityBlock {

    public static final MapCodec<WeatherVaneBlock> CODEC =
            simpleCodec(WeatherVaneBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    private static final VoxelShape SHAPE = Block.box(6, 0, 6, 10, 15, 10);

    public WeatherVaneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.WEATHER_VANE_BE.get(),
                WeatherVaneBlockEntity::tick);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos posBelow = pPos.below();
        return pLevel.getBlockState(posBelow).isFaceSturdy(pLevel, posBelow, Direction.UP);
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos)) {
            pLevel.scheduleTick(pCurrentPos, this, 1);
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!this.canSurvive(pState, pLevel, pPos)) {
            pLevel.destroyBlock(pPos, true);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.WEATHER_VANE_BE.get().create(pPos, pState);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {

        if (pLevel.isClientSide() || !(pLevel instanceof ServerLevel serverLevel)) {
            return 0;
        }

        GameRules gameRules = serverLevel.getGameRules();

        if (!gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
            return 0;
        }

        WindDirectionSavedData windData = WindDirectionSavedData.get(serverLevel);
        int windDirectionIndex = windData.getCurrentDirection();

        // 0 (N) -> 1
        // 1 (NE) -> 3
        // 2 (E) -> 5
        // ...
        // 7 (NW) -> 15
        int signalStrength = (windDirectionIndex * 2) + 1;

        return signalStrength;
    }
}
