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

    // Direction
    private int currentDirection = 0; // 0=N, 1=NE, ..., 7=NW
    private long nextChangeTick = 0;

    // Strength
    private int currentStrength = 0; // 0-4
    private long nextStrengthChangeTick = 0;

    // Storm anchor
    private int stormAnchorDirection = 0;
    private boolean stormAnchorInitialized = false;


    public static WindDirectionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        WindDirectionSavedData data = new WindDirectionSavedData();

        data.currentDirection = tag.getInt("windDirection");
        data.nextChangeTick = tag.getLong("nextChangeTick");
        data.currentStrength = tag.getInt("windStrength");
        data.nextStrengthChangeTick = tag.getLong("nextStrengthChangeTick");

        // Načítání kotvy
        if (tag.contains("stormAnchorDirection", CompoundTag.TAG_INT)) {
            data.stormAnchorDirection = tag.getInt("stormAnchorDirection");
        }
        if (tag.contains("stormAnchorInitialized", CompoundTag.TAG_BYTE)) {
            data.stormAnchorInitialized = tag.getBoolean("stormAnchorInitialized");
        }

        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("windDirection", this.currentDirection);
        tag.putLong("nextChangeTick", this.nextChangeTick);
        tag.putInt("windStrength", this.currentStrength);
        tag.putLong("nextStrengthChangeTick", this.nextStrengthChangeTick);

        // Anchor save
        tag.putInt("stormAnchorDirection", this.stormAnchorDirection);
        tag.putBoolean("stormAnchorInitialized", this.stormAnchorInitialized);

        return tag;
    }

    // --- Direction methods ---

    public void setRandomDirectionAndPlanNext(Level level, int duration) {
        setRandomDirectionInternal(level.random);
        this.nextChangeTick = level.getGameTime() + duration;
        setDirty();
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

    // --- Strength methods ---
    public void setRandomStrengthInternal(ServerLevel level) {
        RandomSource random = level.random;
        int newStrength;

        if (level.isThundering()) {
            newStrength = 4;
        } else if (level.isRaining()) {
            float chance = random.nextFloat();
            if (chance < 0.70f) newStrength = 2;
            else if (chance < 0.95f) newStrength = 3;
            else newStrength = 4;
        } else {
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

    // Anchor
    public int getStormAnchorDirection() {
        return this.stormAnchorDirection;
    }

    public void setStormAnchorDirection(int direction) {
        this.stormAnchorDirection = direction;
        setDirty();
    }

    public boolean isStormAnchorInitialized() {
        return this.stormAnchorInitialized;
    }

    public void setStormAnchorInitialized(boolean initialized) {
        this.stormAnchorInitialized = initialized;
        setDirty();
    }



    public static WindDirectionSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();

        return storage.computeIfAbsent(
                new SavedData.Factory<>(WindDirectionSavedData::new, WindDirectionSavedData::load, DataFixTypes.LEVEL),
                DATA_NAME);
    }
}
