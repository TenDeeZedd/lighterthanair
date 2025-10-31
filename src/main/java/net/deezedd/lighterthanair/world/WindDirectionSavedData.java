package net.deezedd.lighterthanair.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.core.HolderLookup;

import java.util.Random;

public class WindDirectionSavedData extends SavedData {

    private static final String DATA_NAME = "dzlta_wind_direction"; // Unikátní jméno pro naše data
    private int currentDirection = 0; // 0=N, 1=NE, ..., 7=NW
    private final Random random = new Random();

    private long nextChangeTick = 0; // Kdy má nastat příští změna

    public static WindDirectionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        WindDirectionSavedData data = new WindDirectionSavedData();
        if (tag.contains("windDirection", CompoundTag.TAG_INT)) {
            data.currentDirection = tag.getInt("windDirection");
        } else {
            data.setRandomDirectionInternal(); // Použijeme interní metodu
        }

        // Načteme i čas příští změny
        if (tag.contains("nextChangeTick", CompoundTag.TAG_LONG)) {
            data.nextChangeTick = tag.getLong("nextChangeTick");
        }

        return data;
    }

    // Metoda pro uložení dat do NBT
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("windDirection", this.currentDirection);
        // Uložíme i čas příští změny
        tag.putLong("nextChangeTick", this.nextChangeTick);
        return tag;
    }

    public void setRandomDirectionAndPlanNext(Level level, int duration) {
        setRandomDirectionInternal(); // Změníme směr
        this.nextChangeTick = level.getGameTime() + duration;
        setDirty(); // Uložíme změny
    }

    public long getNextChangeTick() {
        return this.nextChangeTick;
    }

    // Interní metoda, která jen změní směr
    private void setRandomDirectionInternal() {
        this.currentDirection = random.nextInt(8); // 0-7
        setDirty();
    }

    public void setDirection(int direction) {
        if (direction >= 0 && direction <= 7) {
            this.currentDirection = direction;
            setDirty();
        }
    }

    // Metoda pro nastavení času (použije se např. při bouřce)
    public void setNextChangeTick(long tick) {
        this.nextChangeTick = tick;
        setDirty();
    }

    // Metoda pro získání aktuálního směru
    public int getCurrentDirection() {
        return this.currentDirection;
    }

    public static WindDirectionSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();

        return storage.computeIfAbsent(
                new SavedData.Factory<>(WindDirectionSavedData::new, WindDirectionSavedData::load, DataFixTypes.LEVEL),
                DATA_NAME);
    }

}
