package net.deezedd.lighterthanair.block;

import com.mojang.serialization.MapCodec;
import net.deezedd.lighterthanair.entity.SmallBalloonEntity;
import net.deezedd.lighterthanair.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class BalloonCrateBlock extends HorizontalDirectionalBlock {

    private final String colorName;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final MapCodec<BalloonCrateBlock> CODEC = simpleCodec(BalloonCrateBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public BalloonCrateBlock(BlockBehaviour.Properties pProperties) {
        // Tento konstruktor je vyžadován pro MapCodec, ale my ho nepoužijeme
        this(pProperties, "white"); // Default na bílou
    }

    public BalloonCrateBlock(BlockBehaviour.Properties pProperties, String colorName) {
        super(pProperties);
        this.colorName = colorName; // Uložíme barvu
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // Balloon spawn logic
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {

        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // TODO: Zde bude kontrola volného místa (isSpaceClear)

        pLevel.removeBlock(pPos, false);

        if (pLevel instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5,
                    1, 0, 0, 0, 0);
            serverLevel.playSound(null, pPos, ModSounds.CRATE_POP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        // 3. Spawne se entita balónu
        SmallBalloonEntity balloon = new SmallBalloonEntity(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5);

        // 4. Nastavíme jí barvu podle této bedny
        balloon.setColor(this.colorName);

        pLevel.addFreshEntity(balloon);

        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}