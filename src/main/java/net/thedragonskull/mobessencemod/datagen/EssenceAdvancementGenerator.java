package net.thedragonskull.mobessencemod.datagen;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
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
import java.util.stream.Collectors;

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
                        ResourceLocation.parse("minecraft:textures/block/deepslate_tiles.png"),
                        FrameType.TASK,
                        true, true, true)
                .addCriterion("has_totem", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(ModItems.TOTEM_OF_ESSENCE.get()).build()
                ))
                .save(saver, ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "root"), helper);

        Advancement.Builder allTotemsBuilder = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.TOTEM_TAB_ICON.get(),
                        Component.literal("Collect 'em all!"),
                        Component.literal("Capture the essence of every creature"),
                        null,
                        FrameType.CHALLENGE,
                        true, true, false)
                .requirements(RequirementsStrategy.AND);

        for (TotemEssenceRegistry.EssenceData essence : TotemEssenceRegistry.getAll()) {
            String id = essence.id().getPath();

            allTotemsBuilder.addCriterion("has_" + id, InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item()
                            .of(ModItems.TOTEM_OF_ESSENCE.get())
                            .hasNbt(TotemUtils.makeEssenceTag(essence.id()))
                            .build()
            ));
        }

        Advancement allTotems = allTotemsBuilder.save(
                saver,
                ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "all_totems"),
                helper
        );

        // PASSIVE
        List<TotemEssenceRegistry.EssenceData> passiveEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.PASSIVE)
                .toList();

        Advancement passiveRoot = generateCategoryAdvancement(
                saver, helper, root,
                "passive", Items.LIME_CONCRETE_POWDER,
                "Passive Mob Essences", "Collect the essence of every passive creature",
                passiveEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.PASSIVE, "passive");

        // NEUTRAL
        List<TotemEssenceRegistry.EssenceData> neutralEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.NEUTRAL)
                .toList();

        Advancement neutralRoot = generateCategoryAdvancement(
                saver, helper, root,
                "neutral", Items.YELLOW_CONCRETE_POWDER,
                "Neutral Mob Essences", "Collect the essence of every neutral creature",
                neutralEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.NEUTRAL, "neutral");

        // HOSTILE
        List<TotemEssenceRegistry.EssenceData> hostileEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.HOSTILE)
                .toList();

        Advancement hostileRoot = generateCategoryAdvancement(
                saver, helper, root,
                "hostile", Items.RED_CONCRETE_POWDER,
                "Hostile Mob Essences", "Collect the essence of every hostile creature",
                hostileEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.HOSTILE, "hostile");

        // SPECIAL
        List<TotemEssenceRegistry.EssenceData> specialEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.SPECIAL)
                .toList();

        Advancement specialRoot = generateCategoryAdvancement(
                saver, helper, root,
                "special", Items.LIGHT_BLUE_CONCRETE_POWDER,
                "Special Mob Essences", "Collect the essence of every special creature",
                specialEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.SPECIAL, "special");

        // BOSS
        List<TotemEssenceRegistry.EssenceData> bossEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.BOSS)
                .toList();

        Advancement bossRoot = generateCategoryAdvancement(
                saver, helper, root,
                "boss", Items.MAGENTA_CONCRETE_POWDER,
                "Boss Mob Essences", "Collect the essence of every boss",
                bossEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.BOSS, "boss");

        // NON_MOB
        List<TotemEssenceRegistry.EssenceData> nonMobEssences = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == TotemMobCategory.NON_MOB)
                .toList();

        Advancement nonMobRoot = generateCategoryAdvancement(
                saver, helper, root,
                "non_mob", Items.WHITE_CONCRETE_POWDER,
                "Non Mob Mob Essences", "Collect the essence of every non mob creature",
                nonMobEssences
        );

        generateEssenceAdvancementsForCategory(saver, helper, allTotems, TotemMobCategory.NON_MOB, "non_mob");
    }

    private Advancement generateCategoryAdvancement(Consumer<Advancement> saver, ExistingFileHelper helper, Advancement parent,
                                                    String id, Item icon, String title, String description,
                                                    List<TotemEssenceRegistry.EssenceData> categoryTotems) {

        Advancement.Builder builder = Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        icon,
                        Component.literal(title),
                        Component.literal(description),
                        null,
                        FrameType.GOAL,
                        true, true, false)
                .requirements(RequirementsStrategy.AND);

        for (TotemEssenceRegistry.EssenceData essence : categoryTotems) {
            String mobId = essence.id().getPath();
            builder.addCriterion("has_" + mobId, InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item()
                            .of(ModItems.TOTEM_OF_ESSENCE.get())
                            .hasNbt(TotemUtils.makeEssenceTag(essence.id()))
                            .build()
            ));
        }

        return builder.save(saver, ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, id + "/root"), helper);
    }

    Map<String, Advancement> generatedParents = new HashMap<>();

    private void generateEssenceAdvancementsForCategory(Consumer<Advancement> saver, ExistingFileHelper helper,
                                                        Advancement root, TotemMobCategory category, String categoryId) {
        Map<String, TotemEssenceRegistry.EssenceData> mobMap = TotemEssenceRegistry.getAll().stream()
                .filter(e -> e.category() == category)
                .collect(Collectors.toMap(e -> e.id().getPath(), e -> e));

        List<String> sortedMobIds = topologicalSort(mobMap.keySet());

        for (String mobId : sortedMobIds) {
            TotemEssenceRegistry.EssenceData essence = mobMap.get(mobId);
            if (essence == null) continue;

            Advancement parent = SUBGROUPS.containsKey(mobId)
                    ? generatedParents.getOrDefault(SUBGROUPS.get(mobId), root)
                    : root;

            if (parent.getId().getPath().matches(".*/root$")) {
                parent = root;
            }

            Advancement adv = generateAdvancement(saver, helper, essence, categoryId, parent, mobId);
            generatedParents.put(mobId, adv);
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

    private List<String> topologicalSort(Set<String> nodes) {
        List<String> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String node : nodes) {
            visit(node, visited, sorted, nodes);
        }
        return sorted;
    }

    private void visit(String node, Set<String> visited, List<String> sorted, Set<String> validNodes) {
        if (visited.contains(node)) return;
        visited.add(node);

        String parent = SUBGROUPS.get(node);
        if (parent != null && validNodes.contains(parent)) {
            visit(parent, visited, sorted, validNodes);
        }

        sorted.add(node);
    }

    private static final Map<String, String> SUBGROUPS = new HashMap<>();

    static {
            SUBGROUPS.put("salmon", "cod");
            SUBGROUPS.put("tropical_fish", "cod");
            SUBGROUPS.put("pufferfish", "cod");
            SUBGROUPS.put("glow_squid", "squid");
            SUBGROUPS.put("donkey", "horse");
            SUBGROUPS.put("mule", "donkey");
            SUBGROUPS.put("cat", "ocelot");
            SUBGROUPS.put("snow_fox", "fox");
            SUBGROUPS.put("temperate_frog", "tadpole");
            SUBGROUPS.put("warm_frog", "tadpole");
            SUBGROUPS.put("cold_frog", "tadpole");
            SUBGROUPS.put("parrot", "chicken");
            SUBGROUPS.put("cave_spider", "spider");
            SUBGROUPS.put("brown_mooshroom", "cow");
            SUBGROUPS.put("red_mooshroom", "cow");
            SUBGROUPS.put("elder_guardian", "guardian");
            SUBGROUPS.put("trader_llama", "llama");
            SUBGROUPS.put("endermite", "silverfish");

            SUBGROUPS.put("wandering_trader", "villager");
            SUBGROUPS.put("iron_golem", "villager");
            SUBGROUPS.put("witch", "villager");

            SUBGROUPS.put("piglin", "pig");
            SUBGROUPS.put("hoglin", "pig");
            SUBGROUPS.put("zoglin", "hoglin");
            SUBGROUPS.put("piglin_brute", "piglin");
            SUBGROUPS.put("zombified_piglin", "piglin");

            SUBGROUPS.put("zombie_villager", "zombie");
            SUBGROUPS.put("zombie_horse", "zombie");
            SUBGROUPS.put("drowned", "zombie");
            SUBGROUPS.put("husk", "zombie");

            SUBGROUPS.put("allay", "pillager");
            SUBGROUPS.put("vex", "pillager");
            SUBGROUPS.put("vindicator", "pillager");
            SUBGROUPS.put("ravager", "pillager");
            SUBGROUPS.put("evoker", "pillager");

            SUBGROUPS.put("skeleton_horse", "skeleton");
            SUBGROUPS.put("stray", "skeleton");
            SUBGROUPS.put("wither_skeleton", "skeleton");
    }

}
