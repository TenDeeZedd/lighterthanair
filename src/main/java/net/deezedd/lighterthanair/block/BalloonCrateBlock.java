package net.deezedd.lighterthanair.block;

import com.mojang.serialization.MapCodec;
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
    // Používáme standardní FACING property
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // Codec pro registraci bloku
    public static final MapCodec<BalloonCrateBlock> CODEC = simpleCodec(BalloonCrateBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public BalloonCrateBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        // Defaultně se dívá na sever
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    // --- Logika spawnování ---

    // ===== OPRAVA ZDE: Toto je správná metoda =====
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        // Logika běží jen na serveru
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS; // Na klientu řekneme, že to proběhlo
        }

        // TODO: Zde bude kontrola volného místa (isSpaceClear)

        // 1. Zničíme blok (false = neshodí item)
        pLevel.removeBlock(pPos, false);

        // 2. Spustíme particly výbuchu
        if (pLevel instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5,
                    1, 0, 0, 0, 0);
            serverLevel.playSound(null, pPos, ModSounds.CRATE_POP.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        // 3. TODO: Zde bude spawn entity balónu

        return InteractionResult.CONSUME; // Server řekne, že akce proběhla
    }
    // ===========================================

    // --- Logika otáčení (Mirror/Rotate) ---
    // Tato metoda se volá při položení bloku, aby se správně otočil k hráči
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    // Tato metoda se volá, když se struktura (nebo hráč) pokusí blok otočit
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    // Tato metoda se volá, když se struktura (nebo hráč) pokusí blok zrcadlit
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    // Zaregistrujeme FACING property
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }
}