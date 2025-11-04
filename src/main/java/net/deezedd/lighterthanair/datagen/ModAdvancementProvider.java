package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {

    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new LtaAdvancementGenerator()));
    }

    private static final class LtaAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

            // Root advancement
            AdvancementHolder rootAdvancement = Advancement.Builder.advancement()
                    .display(
                            new ItemStack(ModItems.WEATHER_VANE_ITEM.get()),
                            Component.translatable("advancements.dzlta.root.title"),
                            Component.translatable("advancements.dzlta.root.description"),
                            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            false,
                            false,
                            false
                    )

                    // Unlock requirement
                    .addCriterion("unlocked", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "root"), existingFileHelper);

            // Weather Vane Advancement
            Advancement.Builder.advancement()
                    .parent(rootAdvancement)
                    .display(
                            new ItemStack(ModItems.WEATHER_VANE_ITEM.get()),
                            Component.translatable("advancements.dzlta.get_weather_vane.title"),
                            Component.translatable("advancements.dzlta.get_weather_vane.description"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("has_weather_vane", InventoryChangeTrigger.TriggerInstance.hasItems(
                            ModItems.WEATHER_VANE_ITEM.get()
                    ))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "get_weather_vane"), existingFileHelper);
        }
    }
}
