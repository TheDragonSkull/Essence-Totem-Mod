package net.thedragonskull.mobessencemod.datagen.loot;

import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetInstrumentFunction;
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

        consumer.accept(ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "advancements/neutral"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.CONDUIT)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        )

                        // (1 of 4 scream variants)
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.GOAT_HORN)
                                        .apply(SetInstrumentFunction.setInstrumentOptions(InstrumentTags.SCREAMING_GOAT_HORNS)))
                        )
        );

        consumer.accept(ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "advancements/hostile"),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.NETHERITE_SWORD)
                                        .apply(new SetEnchantmentsFunction.Builder()
                                                .withEnchantment(Enchantments.SHARPNESS, ConstantValue.exactly(5))
                                        )
                                )
                        )
        );


    }
}
