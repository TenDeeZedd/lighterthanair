package net.deezedd.lighterthanair.blockentity;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.sound.ModSounds;
import net.deezedd.lighterthanair.util.ModGameRules;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;


public class WeatherVaneBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int lastKnownDirection = 0;
    private int stormCreakChangeCounter = 0;

    public WeatherVaneBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WEATHER_VANE_BE.get(), pPos, pBlockState);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WeatherVaneBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        GameRules gameRules = serverLevel.getGameRules();

        int currentDirection;

        if (!gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
            currentDirection = 0;
        } else {
            currentDirection = WindDirectionSavedData.get(serverLevel).getCurrentDirection();
        }

        if (currentDirection != blockEntity.lastKnownDirection) {
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
            boolean chaoticStorms = gameRules.getBoolean(ModGameRules.RULE_WINDDIRECTIONCHAOTICSTORMS);

            if (chaoticStorms && serverLevel.isThundering()) {

                if (blockEntity.stormCreakChangeCounter <= 0) {
                    playRandomCreakSound(serverLevel, pos);
                    blockEntity.stormCreakChangeCounter = serverLevel.random.nextInt(4) + 2;

                } else {
                    blockEntity.stormCreakChangeCounter--;
                }

            } else {
                playRandomCreakSound(serverLevel, pos);
            }

            blockEntity.lastKnownDirection = currentDirection;
            blockEntity.setChanged();
        }
    }

    private static void playRandomCreakSound(Level level, BlockPos pos) {
        if (ModSounds.VANE_CREAKS.isEmpty()) return;

        SoundEvent sound = ModSounds.VANE_CREAKS.get(level.random.nextInt(ModSounds.VANE_CREAKS.size())).get();
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.7f, 1.0f);
    }

}
