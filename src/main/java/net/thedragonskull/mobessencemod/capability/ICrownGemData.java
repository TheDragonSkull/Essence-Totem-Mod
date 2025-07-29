package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;

public interface ICrownGemData {
    boolean hasGem(String key);
    void setGem(String key, boolean value);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
