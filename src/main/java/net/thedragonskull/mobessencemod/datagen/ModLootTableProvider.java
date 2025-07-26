package net.thedragonskull.mobessencemod.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.thedragonskull.mobessencemod.datagen.loot.ModAdvancementLootTables;
import net.thedragonskull.mobessencemod.datagen.loot.ModBlockLootTables;

import java.util.List;
import java.util.Set;

public class ModLootTableProvider {

    public static LootTableProvider create(PackOutput output) {
        return new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(ModAdvancementLootTables::new, LootContextParamSets.ADVANCEMENT_REWARD)
        ));
    }

}
