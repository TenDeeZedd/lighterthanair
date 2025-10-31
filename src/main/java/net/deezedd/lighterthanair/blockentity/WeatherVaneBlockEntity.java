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

    // Paměť pro detekci změny
    private int lastKnownDirection = 0;
    // Čítač pro chaotické zvuky (kolik změn zbývá do zvuku)
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
        // Logika běží jen na serveru
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;
        GameRules gameRules = serverLevel.getGameRules();

        int currentDirection;

        // 1. Zjistíme, jaký je aktuální směr
        if (!gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
            currentDirection = 0; // Pokud je vítr vypnutý, cíl je 0 (Sever)
        } else {
            currentDirection = WindDirectionSavedData.get(serverLevel).getCurrentDirection();
        }

        // 2. Porovnáme s posledním známým směrem
        if (currentDirection != blockEntity.lastKnownDirection) {
            // ZMĚNA NASTALA!

            boolean chaoticStorms = gameRules.getBoolean(ModGameRules.RULE_WINDCHAOTICSTORMS);

            // 3. Rozhodneme, jestli přehrát zvuk
            if (chaoticStorms && serverLevel.isThundering()) {
                // Jsme v bouřce
                if (blockEntity.stormCreakChangeCounter <= 0) {
                    // Čas přehrát zvuk
                    playRandomCreakSound(serverLevel, pos);
                    // Nastavit nový náhodný čítač (2-5 změn)
                    blockEntity.stormCreakChangeCounter = serverLevel.random.nextInt(4) + 2; // 2, 3, 4, 5
                } else {
                    // Ještě není čas, jen snížíme čítač
                    blockEntity.stormCreakChangeCounter--;
                }
            } else {
                // Nejsme v bouřce, přehrajeme zvuk vždy
                playRandomCreakSound(serverLevel, pos);
            }

            // 4. Uložíme si nový směr
            blockEntity.lastKnownDirection = currentDirection;
            blockEntity.setChanged(); // Dáme vědět, že se BE změnilo (i když nic neukládáme)
        }
    }

    private static void playRandomCreakSound(Level level, BlockPos pos) {
        if (ModSounds.VANE_CREAKS.isEmpty()) return; // Pojistka

        // Vybereme náhodný zvuk z našeho seznamu
        SoundEvent sound = ModSounds.VANE_CREAKS.get(level.random.nextInt(ModSounds.VANE_CREAKS.size())).get();

        // Přehrajeme ho na pozici bloku
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.7f, 1.0f); // Hlasitost 70%
    }

}
