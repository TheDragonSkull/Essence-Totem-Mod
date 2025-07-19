package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;

public interface IMobEssenceData {
    void setKeepInventory(boolean value);
    boolean shouldKeepInventory();

    void storeInventory(Inventory inv);
    void restoreInventory(Inventory inv);

    void readFromNBT(CompoundTag nbt);
    void writeToNBT(CompoundTag nbt);
}
