package net.deezedd.lighterthanair.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.core.HolderLookup;

import java.util.Random;

public class WindDirectionSavedData extends SavedData {

    private static final String DATA_NAME = "dzlta_wind_direction";
    private final Random random = new Random();

    // Směr
    private int currentDirection = 0; // 0=N, 1=NE, ..., 7=NW
    private long nextChangeTick = 0; // Kdy má nastat příští změna směru

    // Síla
    private int currentStrength = 0; // 0-4
    private long nextStrengthChangeTick = 0; // Kdy má nastat příští změna síly


    public static WindDirectionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        WindDirectionSavedData data = new WindDirectionSavedData();

        // Načítání směru
        if (tag.contains("windDirection", CompoundTag.TAG_INT)) {
            data.currentDirection = tag.getInt("windDirection");
        }

        if (tag.contains("nextChangeTick", CompoundTag.TAG_LONG)) {
            data.nextChangeTick = tag.getLong("nextChangeTick");
        }

        // Načítání síly
        if (tag.contains("windStrength", CompoundTag.TAG_INT)) {
            data.currentStrength = tag.getInt("windStrength");
        }
        if (tag.contains("nextStrengthChangeTick", CompoundTag.TAG_LONG)) {
            data.nextStrengthChangeTick = tag.getLong("nextStrengthChangeTick");
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // Ukládání směru
        tag.putInt("windDirection", this.currentDirection);
        tag.putLong("nextChangeTick", this.nextChangeTick);

        // Ukládání síly
        tag.putInt("windStrength", this.currentStrength);
        tag.putLong("nextStrengthChangeTick", this.nextStrengthChangeTick);

        return tag;
    }

    // --- Metody pro Směr ---

    public void setRandomDirectionAndPlanNext(Level level, int duration) {
        setRandomDirectionInternal(level.random);
        this.nextChangeTick = level.getGameTime() + duration;
        setDirty(); // Uložíme změny
    }

    public long getNextChangeTick() {
        return this.nextChangeTick;
    }

    public void setRandomDirectionInternal(RandomSource random) {
        this.currentDirection = random.nextInt(8); // 0-7
        setDirty();
    }

    public void setDirection(int direction) {
        if (direction >= 0 && direction <= 7) {
            this.currentDirection = direction;
            setDirty();
        }
    }

    public void setNextChangeTick(long tick) {
        this.nextChangeTick = tick;
        setDirty();
    }

    public int getCurrentDirection() {
        return this.currentDirection;
    }

    // --- Metody pro Sílu ---

    public void setRandomStrengthInternal(ServerLevel level) {
        RandomSource random = level.random;
        int newStrength;

        if (level.isThundering()) {
            // Bouřka: Vždy síla 4
            newStrength = 4;
        } else if (level.isRaining()) {
            // Déšť: 70% šance na 2, 25% na 3, 5% na 4
            float chance = random.nextFloat();
            if (chance < 0.70f) newStrength = 2;
            else if (chance < 0.95f) newStrength = 3;
            else newStrength = 4;
        } else {
            // Jasno: 10% šance na 0, 60% na 1, 30% na 2
            float chance = random.nextFloat();
            if (chance < 0.10f) newStrength = 0;
            else if (chance < 0.70f) newStrength = 1;
            else newStrength = 2;
        }

        this.setStrength(newStrength);
    }

    public void setRandomStrengthAndPlanNext(ServerLevel level, int duration) {
        setRandomStrengthInternal(level);
        this.nextStrengthChangeTick = level.getGameTime() + duration;
        setDirty();
    }

    public void setStrengthAndPlanNext(ServerLevel level, int strength, int duration) {
        setStrength(strength);
        this.nextStrengthChangeTick = level.getGameTime() + duration;
        setDirty();
    }


    public void setStrength(int strength) {
        if (strength >= 0 && strength <= 4) {
            this.currentStrength = strength;
            setDirty();
        }
    }

    public int getCurrentStrength() {
        return this.currentStrength;
    }

    public void setNextStrengthChangeTick(long tick) {
        this.nextStrengthChangeTick = tick;
        setDirty();
    }

    public long getNextStrengthChangeTick() {
        return this.nextStrengthChangeTick;
    }

    // --- Statická metoda ---

    public static WindDirectionSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();

        return storage.computeIfAbsent(
                new SavedData.Factory<>(WindDirectionSavedData::new, WindDirectionSavedData::load, DataFixTypes.LEVEL),
                DATA_NAME);
    }
}
