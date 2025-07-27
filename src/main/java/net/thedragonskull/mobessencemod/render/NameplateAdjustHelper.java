package net.thedragonskull.mobessencemod.render;

import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NameplateAdjustHelper {
    private static final Set<UUID> playersToAdjust = new HashSet<>();

    public static void mark(Player player) {
        playersToAdjust.add(player.getUUID());
    }

    public static void unmark(Player player) {
        playersToAdjust.remove(player.getUUID());
    }

    public static boolean shouldAdjust(Player player) {
        return playersToAdjust.contains(player.getUUID());
    }
}
