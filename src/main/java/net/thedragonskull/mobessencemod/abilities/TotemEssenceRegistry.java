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
        register("minecraft:pig", "essence_pig",
                new TotemTooltipData("Cast-Iron Stomach", "Immune to negative food effects"),
                SoundEvents.PIG_HURT,
                new PigAbility()
        );

        register("minecraft:bee", "essence_bee",
                new TotemTooltipData("Stinger Reflex", "Stings and poisons enemies when hit from behind"),
                SoundEvents.BEE_HURT,
                new BeeAbility()
        );

        register("minecraft:zombie", "essence_zombie",
                new TotemTooltipData("Zombie Recall", "Revives on death with half heart (one time use)"),
                SoundEvents.ZOMBIE_HURT,
                new ZombieAbility()
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
