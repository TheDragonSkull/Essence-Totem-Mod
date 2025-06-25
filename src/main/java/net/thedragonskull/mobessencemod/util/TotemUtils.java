package net.thedragonskull.mobessencemod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;

public class TotemUtils {

    // SOUNDS
    public static final Map<String, SoundEvent> MOB_SOUNDS = Map.ofEntries(
            Map.entry("minecraft:pig", SoundEvents.PIG_HURT),
            Map.entry("minecraft:bee", SoundEvents.BEE_HURT)
    );

    public static SoundEvent getSoundForMob(ResourceLocation mobId) {
        return MOB_SOUNDS.getOrDefault(mobId.toString(), SoundEvents.EXPERIENCE_ORB_PICKUP);
    }

    // TOOLTIPS
    public static final Map<String, TotemTooltipData> TOTEM_TOOLTIPS = Map.ofEntries(
            Map.entry("minecraft:pig", new TotemTooltipData("Cast-Iron Stomach", "Immune to negative food effects")),
            Map.entry("minecraft:bee", new TotemTooltipData("Stinger Reflex", "Stings and poisons enemies when hit from behind"))
    );

    public static TotemTooltipData getTooltipForMob(ResourceLocation mobId) {
        return TOTEM_TOOLTIPS.getOrDefault(mobId.toString(),
                new TotemTooltipData("Unknown", "No effect known."));
    }


}
