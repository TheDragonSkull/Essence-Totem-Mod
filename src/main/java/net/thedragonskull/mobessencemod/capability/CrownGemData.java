package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public class CrownGemData implements ICrownGemData {
    private final Map<String, Boolean> gems = new HashMap<>();

    @Override
    public boolean hasGem(String key) {
        return gems.getOrDefault(key, false);
    }

    @Override
    public void setGem(String key, boolean value) {
        gems.put(key, value);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, Boolean> entry : gems.entrySet()) {
            tag.putBoolean(entry.getKey(), entry.getValue());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        gems.clear();
        for (String key : nbt.getAllKeys()) {
            gems.put(key, nbt.getBoolean(key));
        }
    }
}
