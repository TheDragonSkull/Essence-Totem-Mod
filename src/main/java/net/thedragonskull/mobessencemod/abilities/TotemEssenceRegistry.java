package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.thedragonskull.mobessencemod.util.TotemTooltipData;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TotemEssenceRegistry {

    public record EssenceData(
            ResourceLocation id,
            String modelPredicateName,
            TotemTooltipData tooltip,
            SoundEvent sound,
            IMobAbility ability
    ) {}

    private static final Map<String, EssenceData> ESSENCE_DATA = new LinkedHashMap<>();

    static {

        // PASSIVE
        register("minecraft:pig", "essence_pig",
                new TotemTooltipData("Cast-Iron Stomach", "Immune to negative food effects"),
                SoundEvents.PIG_HURT,
                new PigAbility()
        );

        register("minecraft:parrot", "essence_parrot",
                new TotemTooltipData("Featherlight", "Double jump + don't trigger pressure plates or tripwires"),
                SoundEvents.PARROT_HURT,
                new ParrotAbility()
        );

        register("minecraft:cod", "essence_cod",
                new TotemTooltipData("Adaptive Lungs", "Increases underwater breathing time"),
                SoundEvents.COD_HURT,
                new CodAbility()
        );

        register("minecraft:salmon", "essence_salmon",
                new TotemTooltipData("Underwater Momentum", "Increases swimming speed"),
                SoundEvents.SALMON_HURT,
                new SalmonAbility()
        );

        register("minecraft:tropical_fish", "essence_tropical_fish",
                new TotemTooltipData("Clear Waters", "Enhanced underwater visibility"),
                SoundEvents.TROPICAL_FISH_HURT,
                new TropicalFishAbility()
        );

        register("minecraft:chicken", "essence_chicken",
                new TotemTooltipData("Feathered Fiend", "Basically permanent slow falling"),
                SoundEvents.CHICKEN_HURT,
                new ChickenAbility()
        );

        register("minecraft:villager", "essence_villager",
                new TotemTooltipData("Scent of the Money", "Grow a big nose to smell nearby emerald ores (~20 blocks)"),
                SoundEvents.VILLAGER_HURT,
                new VillagerAbility()
        );

        // NEUTRAL
        register("minecraft:bee", "essence_bee",
                new TotemTooltipData("Stinger Reflex", "Stings and poisons enemies when hit from behind"),
                SoundEvents.BEE_HURT,
                new BeeAbility()
        );

        register("minecraft:pufferfish", "essence_pufferfish",
                new TotemTooltipData("Toxic Touch", "Chance to inflict knockback + poison on contact (1/3)"),
                SoundEvents.PUFFER_FISH_HURT,
                new PufferfishAbility()
        );

        register("minecraft:spider", "essence_spider",
                new TotemTooltipData("Wall-Crawler", "Slowly climb vertical surfaces"),
                SoundEvents.SPIDER_HURT,
                new SpiderAbility()
        );

        register("minecraft:cave_spider", "essence_cave_spider",
                new TotemTooltipData("Poisonous Wall-Crawler", "Slowly climb walls + poison enemies when unarmed (20%)"),
                SoundEvents.SPIDER_HURT,
                new CaveSpiderAbility()
        );

        register("minecraft:enderman", "essence_enderman",
                new TotemTooltipData("Blink Instinct", "Teleport away to evade ranged damage"),
                SoundEvents.ENDERMAN_HURT,
                new EndermanAbility()
        );

        register("minecraft:goat", "essence_goat",
                new TotemTooltipData("Ram Charge", "Tackle any mob in your way while running; the faster the player, the more the damage"),
                SoundEvents.GOAT_HURT,
                new GoatAbility()
        );

        // HOSTILE
        register("minecraft:zombie", "essence_zombie",
                new TotemTooltipData("Zombie Recall", "Revive with half heart (one time use)"),
                SoundEvents.ZOMBIE_HURT,
                new ZombieAbility()
        );

        register("minecraft:creeper", "essence_creeper",
                new TotemTooltipData("Explosive Retaliation", "When taking melee damage, there's a chance to trigger a small explosion that doesn't affect blocks"),
                SoundEvents.CREEPER_HURT,
                new CreeperAbility()
        );

        register("minecraft:skeleton", "essence_skeleton",
                new TotemTooltipData("Bone Quiver", "Arrows fired have a chance to not be consumed: 50% for regular arrows, 25% for spectral or tipped arrows"),
                SoundEvents.SKELETON_HURT,
                new SkeletonAbility()
        );

        register("minecraft:slime", "essence_slime",
                new TotemTooltipData("Elastic Body", "Bounce on the ground when falling + chance to absorbe melee damage"),
                SoundEvents.SLIME_HURT,
                new SlimeAbility()
        );

        // SPECIAL
        register("minecraft:zombie_villager", "essence_zombie_villager",
                new TotemTooltipData("Undead Scent of the Money", "Grow a big nose to smell nearby emerald ores (~20 blocks) + revive with half heart (one time use)"),
                SoundEvents.ZOMBIE_VILLAGER_HURT,
                new ZombieVillagerAbility()
        );

    }

    private static void register(String id, String predicate, TotemTooltipData tooltip, SoundEvent sound, IMobAbility ability) {
        ESSENCE_DATA.put(id, new EssenceData(
                ResourceLocation.parse(id),
                predicate,
                tooltip,
                sound,
                ability
        ));
    }

    public static boolean isRegistered(ResourceLocation id) {
        return ESSENCE_DATA.containsKey(id.toString());
    }

    public static Set<String> getRegisteredEssenceIds() {
        return ESSENCE_DATA.keySet();
    }

    public static EssenceData get(ResourceLocation id) {
        return ESSENCE_DATA.get(id.toString());
    }

    public static Collection<EssenceData> getAll() {
        return ESSENCE_DATA.values();
    }

}
