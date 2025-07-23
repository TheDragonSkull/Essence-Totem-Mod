package net.thedragonskull.mobessencemod.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.item.ModItems;

import java.util.Optional;
import java.util.function.Consumer;

public class EssenceAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper helper) {

        ItemStack icon = new ItemStack(ModItems.TOTEM_OF_ESSENCE.get());

        Advancement root = Advancement.Builder.advancement()
                .display(
                        icon,
                        Component.literal("Mob Essence"),
                        Component.literal("Unlock the secrets of creature essences"),
                        ResourceLocation.parse("minecraft:textures/gui/advancements/backgrounds/adventure.png"),
                        FrameType.TASK,
                        true, true, true)
                .addCriterion("has_totem", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ModItems.TOTEM_OF_ESSENCE.get()).build()
                ))
                .save(saver, ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "root"), helper);

        generateCategoryAdvancement(saver, helper, root,
                "passive", Items.LIME_CONCRETE_POWDER, "Passive Mob Essences", "Collect the essence of every passive creature");

        generateCategoryAdvancement(saver, helper, root,
                "neutral", Items.YELLOW_CONCRETE_POWDER, "Neutral Mob Essences", "Collect the essence of every neutral creature");

        generateCategoryAdvancement(saver, helper, root,
                "hostile", Items.RED_CONCRETE_POWDER, "Hostile Mob Essences", "Collect the essence of every hostile creature");

        generateCategoryAdvancement(saver, helper, root,
                "special", Items.LIGHT_BLUE_CONCRETE_POWDER, "Special Mob Essences", "Collect the essence of every special creature");

        generateCategoryAdvancement(saver, helper, root,
                "boss", Items.MAGENTA_CONCRETE_POWDER, "Boss Mob Essences", "Collect the essence of every boss");

        generateCategoryAdvancement(saver, helper, root,
                "non_mob", Items.WHITE_CONCRETE_POWDER, "Non Mob Mob Essences", "Collect the essence of every non mob creature");
    }

    private void generateCategoryAdvancement(Consumer<Advancement> saver, ExistingFileHelper helper, Advancement parent,
                                             String id, Item icon, String title, String description) {

        Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        icon,
                        Component.literal(title),
                        Component.literal(description),
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_" + id, InventoryChangeTrigger.TriggerInstance.hasItems(icon))
                .save(saver, ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, id + "/root"), helper);
    }
}
