package net.thedragonskull.mobessencemod.datagen.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.thedragonskull.mobessencemod.MobEssenceMod;

import java.util.function.BiConsumer;

public class ModAdvancementLootTables implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {

        consumer.accept(ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "advancements/passive"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.NAME_TAG)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5))))
                        )
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.SADDLE)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        )
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.DIAMOND_HORSE_ARMOR)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        )
        );

    }
}
