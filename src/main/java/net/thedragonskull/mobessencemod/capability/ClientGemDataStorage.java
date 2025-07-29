package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.*;

public class ClientGemDataStorage {
    private static final Map<UUID, Set<String>> PLAYER_GEMS = new HashMap<>();

    public static void receive(UUID uuid, CompoundTag tag) {
        Set<String> unlocked = new HashSet<>();

        for (String key : tag.getAllKeys()) {
            if (tag.getBoolean(key)) {
                unlocked.add(key);
            }
        }

        PLAYER_GEMS.put(uuid, unlocked);
    }

    public static boolean hasGem(UUID playerId, String gemId) {
        return PLAYER_GEMS.getOrDefault(playerId, Set.of()).contains(gemId);
    }
}
