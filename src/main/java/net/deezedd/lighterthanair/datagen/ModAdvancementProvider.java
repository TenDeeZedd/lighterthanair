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

    // Konstruktor, který přijímá argumenty z GatherDataEvent
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        // Předáme instanci našeho generátoru do Listu
        super(output, lookupProvider, existingFileHelper, List.of(new LtaAdvancementGenerator()));
    }

    // Vnitřní třída, která bude generovat naše advancementy
    private static final class LtaAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

            // --- KROK 1: VYTVOŘENÍ ROOT ADVANCEMENTU (NAŠE NOVÁ ZÁLOŽKA) ---
            // Tento advancement definuje samotnou záložku
            AdvancementHolder rootAdvancement = Advancement.Builder.advancement()
                    .display(
                            new ItemStack(ModItems.WEATHER_VANE_ITEM.get()), // Ikona záložky (může být i ikona balónu)
                            Component.translatable("advancements.dzlta.root.title"), // Název záložky
                            Component.translatable("advancements.dzlta.root.description"), // Popis záložky
                            // Toto je klíčové: obrázek na pozadí záložky
                            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK, // Root je obvykle TASK
                            false, // Nezobrazovat toast
                            false, // Neoznamovat do chatu
                            false  // Není skrytý
                    )
                    // Root musí mít také nějakou podmínku, aby se odemkl.
                    // Nejjednodušší je použít "tick", což ho odemkne téměř okamžitě.
                    // Nebo můžeme použít "inventory_changed" s jakýmkoliv itemem,
                    // ale "tick" je pro root jednodušší.
                    .addCriterion("unlocked", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT)) // Odemkne se sebráním hlíny (můžeme změnit)
                    .save(saver, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "root"), existingFileHelper);


            // --- KROK 2: ÚPRAVA NAŠEHO PŮVODNÍHO ADVANCEMENTU ---
            Advancement.Builder.advancement()
                    // --- PŘIDÁNO: Nastavíme rodiče ---
                    .parent(rootAdvancement)

                    .display(
                            new ItemStack(ModItems.WEATHER_VANE_ITEM.get()),
                            Component.translatable("advancements.dzlta.get_weather_vane.title"),
                            Component.translatable("advancements.dzlta.get_weather_vane.description"),
                            null, // Pozadí už má rodič
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
