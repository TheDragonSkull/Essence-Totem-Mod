package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;

import java.util.ArrayList;
import java.util.List;

public class MobEssenceData implements IMobEssenceData {
    private boolean keepInventory = false;
    private final List<ItemStack> storedItems = new ArrayList<>();
    private CompoundTag curiosData = null;

    @Override
    public void setKeepInventory(boolean value) {
        this.keepInventory = value;
    }

    @Override
    public boolean shouldKeepInventory() {
        return keepInventory;
    }

    @Override
    public void storeInventory(Inventory inv) {
        storedItems.clear();
        for (int i = 0; i < inv.items.size(); i++) {
            storedItems.add(inv.items.get(i).copy());
        }
        for (int i = 0; i < inv.armor.size(); i++) {
            storedItems.add(inv.armor.get(i).copy());
        }
        storedItems.add(inv.offhand.get(0).copy());

        inv.player.getCapability(CuriosCapability.INVENTORY).ifPresent(handler -> {
            curiosData = handler.serializeNBT(); //todo: Cannot resolve method 'deserializeNBT' in 'ICuriosItemHandler' y Cannot resolve method 'serializeNBT' in 'ICuriosItemHandler'

        });
    }

    @Override
    public void restoreInventory(Inventory inv) {
        int index = 0;
        for (int i = 0; i < inv.items.size(); i++) {
            inv.items.set(i, storedItems.get(index++));
        }
        for (int i = 0; i < inv.armor.size(); i++) {
            inv.armor.set(i, storedItems.get(index++));
        }
        inv.offhand.set(0, storedItems.get(index));

        if (curiosData != null) {
            inv.player.getCapability(CuriosCapability.INVENTORY).ifPresent(handler -> {
                CuriosApi.getSlotHelper().readCuriosFromTag(handler, curiosData);
            });
        }

        keepInventory = false;
        storedItems.clear();
        curiosData = null;
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        keepInventory = nbt.getBoolean("KeepInventory");
        storedItems.clear();
        ListTag itemsTag = nbt.getList("StoredItems", Tag.TAG_COMPOUND);
        for (Tag tag : itemsTag) {
            storedItems.add(ItemStack.of((CompoundTag) tag));
        }

        if (nbt.contains("CuriosData", Tag.TAG_COMPOUND)) {
            curiosData = nbt.getCompound("CuriosData");
        } else {
            curiosData = null;
        }
    }

    @Override
    public void writeToNBT(CompoundTag nbt) {
        nbt.putBoolean("KeepInventory", keepInventory);
        ListTag itemsTag = new ListTag();
        for (ItemStack stack : storedItems) {
            itemsTag.add(stack.save(new CompoundTag()));
        }
        nbt.put("StoredItems", itemsTag);

        if (curiosData != null) {
            nbt.put("CuriosData", curiosData);
        }
    }
}