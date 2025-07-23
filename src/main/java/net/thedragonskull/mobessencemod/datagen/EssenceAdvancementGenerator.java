package net.thedragonskull.mobessencemod.datagen;

import net.minecraft.ChatFormatting;
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
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.util.TotemMobCategory;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;
import java.util.function.Consumer;

import static net.thedragonskull.mobessencemod.util.TotemUtils.formatMobName;

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

        Advancement passiveRoot = generateCategoryAdvancement(saver, helper, root,
                "passive", Items.LIME_CONCRETE_POWDER, "Passive Mob Essences", "Collect the essence of every passive creature");

        generateEssenceAdvancementsForCategory(saver, helper, passiveRoot, TotemMobCategory.PASSIVE, "passive");

        Advancement neutralRoot = generateCategoryAdvancement(saver, helper, root,
                "neutral", Items.YELLOW_CONCRETE_POWDER, "Neutral Mob Essences", "Collect the essence of every neutral creature");

        generateEssenceAdvancementsForCategory(saver, helper, neutralRoot, TotemMobCategory.NEUTRAL, "neutral");

        Advancement hostileRoot = generateCategoryAdvancement(saver, helper, root,
                "hostile", Items.RED_CONCRETE_POWDER, "Hostile Mob Essences", "Collect the essence of every hostile creature");

        //generateEssenceAdvancementsForCategory(saver, helper, hostileRoot, TotemMobCategory.HOSTILE, "hostile");

        Advancement specialRoot = generateCategoryAdvancement(saver, helper, root,
                "special", Items.LIGHT_BLUE_CONCRETE_POWDER, "Special Mob Essences", "Collect the essence of every special creature");

        //generateEssenceAdvancementsForCategory(saver, helper, specialRoot, TotemMobCategory.SPECIAL, "special");

        Advancement bossRoot = generateCategoryAdvancement(saver, helper, root,
                "boss", Items.MAGENTA_CONCRETE_POWDER, "Boss Mob Essences", "Collect the essence of every boss");

        //generateEssenceAdvancementsForCategory(saver, helper, bossRoot, TotemMobCategory.BOSS, "boss");

        Advancement nonMobRoot = generateCategoryAdvancement(saver, helper, root,
                "non_mob", Items.WHITE_CONCRETE_POWDER, "Non Mob Mob Essences", "Collect the essence of every non mob creature");

        //generateEssenceAdvancementsForCategory(saver, helper, nonMobRoot, TotemMobCategory.NON_MOB, "non_mob");
    }

    private Advancement generateCategoryAdvancement(Consumer<Advancement> saver, ExistingFileHelper helper, Advancement parent,
                                                    String id, Item icon, String title, String description) {

        return Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        icon,
                        Component.literal(title),
                        Component.literal(description),
                        null,
                        FrameType.GOAL,
                        true, true, false)
                .addCriterion("has_" + id, InventoryChangeTrigger.TriggerInstance.hasItems(icon))
                .save(saver, ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, id + "/root"), helper);
    }

    Map<String, Advancement> generatedParents = new HashMap<>();

    private void generateEssenceAdvancementsForCategory(Consumer<Advancement> saver, ExistingFileHelper helper,
                                                        Advancement root, TotemMobCategory category, String categoryId) {
        List<TotemEssenceRegistry.EssenceData> all = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == category)
                .toList();

        Set<String> generated = new HashSet<>();

        for (TotemEssenceRegistry.EssenceData essence : all) {
            String mobId = essence.id().getPath();
            if (!SUBGROUPS.containsValue(mobId)) continue;

            Advancement advancement = generateAdvancement(
                    saver, helper, essence, categoryId, root, mobId
            );
            generatedParents.put(mobId, advancement);
            generated.add(mobId);
        }

        for (TotemEssenceRegistry.EssenceData essence : all) {
            String mobId = essence.id().getPath();
            if (generated.contains(mobId)) continue;

            Advancement parentAdv = SUBGROUPS.containsKey(mobId)
                    ? generatedParents.get(SUBGROUPS.get(mobId))
                    : root;

            generateAdvancement(saver, helper, essence, categoryId, parentAdv, mobId);
            generated.add(mobId);
        }
    }

    private Advancement generateAdvancement(Consumer<Advancement> saver, ExistingFileHelper helper,
                                            TotemEssenceRegistry.EssenceData essence,
                                            String categoryId, Advancement parent, String mobId) {

        ResourceLocation advancementId = ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, categoryId + "/" + mobId);

        ItemStack stack = new ItemStack(ModItems.TOTEM_OF_ESSENCE.get());
        TotemUtils.setEssence(stack, essence.id());

        Component description = Component.literal("[" + essence.category().toString() + "] ").withStyle(essence.category().asStyle())
                .append(Component.literal(essence.tooltip().getDescription()).withStyle(ChatFormatting.GRAY));

        return Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        stack,
                        Component.literal("Essence of the " + formatMobName(essence.id().getPath())),
                        description,
                        null,
                        FrameType.TASK,
                        true, true, false)
                .addCriterion("has_" + mobId, InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item()
                                .of(ModItems.TOTEM_OF_ESSENCE.get())
                                .hasNbt(TotemUtils.makeEssenceTag(essence.id()))
                                .build()
                ))
                .save(saver, advancementId, helper);
    }

    private static final Map<String, String> SUBGROUPS = Map.ofEntries(
            //passive
            Map.entry("salmon", "cod"),
            Map.entry("tropical_fish", "cod"),
            Map.entry("glow_squid", "squid"),
            Map.entry("donkey", "horse"),
            Map.entry("cat", "ocelot"),
            Map.entry("snow_fox", "fox"),
            Map.entry("temperate_frog", "tadpole"),
            Map.entry("warm_frog", "tadpole"),
            Map.entry("cold_frog", "tadpole"),
            Map.entry("parrot", "chicken"),

            //neutral
            Map.entry("cave_spider", "spider"),
            Map.entry("zombified_piglin", "piglin")
    );

}
