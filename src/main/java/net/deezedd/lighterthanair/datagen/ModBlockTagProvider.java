package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LighterThanAir.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.WEATHER_VANE.get());
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.WIND_COMPASS.get());

        TagsProvider.TagAppender<Block> axeTag = this.tag(BlockTags.MINEABLE_WITH_AXE);

        // Najdeme všechny bloky beden a přidáme je do tagu
        ModBlocks.BLOCKS.getEntries().stream()
                .filter(blockHolder -> blockHolder.getId().getPath().contains("_balloon_crate"))
                .forEach(blockHolder -> ((IntrinsicTagAppender<Block>) axeTag).add(blockHolder.get()));

    }
}