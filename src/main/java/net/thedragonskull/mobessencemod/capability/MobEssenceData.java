package net.thedragonskull.mobessencemod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobEssenceData implements IMobEssenceData {
    private boolean keepInventory = false;
    private final List<ItemStack> storedItems = new ArrayList<>();
    private final Map<String, List<ItemStack>> curiosItems = new HashMap<>();

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

        curiosItems.clear();
        CuriosApi.getCuriosInventory(inv.player).ifPresent(handler -> {
            System.out.println("check 1 store");

            handler.getCurios().forEach((id, stacksHandler) -> {
                System.out.println("check 2 store");

                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < stacksHandler.getStacks().getSlots(); i++) {
                    list.add(stacksHandler.getStacks().getStackInSlot(i).copy());
                }
                curiosItems.put(id, list);
            });
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

        CuriosApi.getCuriosInventory(inv.player).ifPresent(handler -> {
            System.out.println("check 1 restore");

            curiosItems.forEach((id, list) -> {
                System.out.println("check 2 restore");

                ICurioStacksHandler stacksHandler = handler.getStacksHandler(id).orElse(null);
                if (stacksHandler != null) {
                    for (int i = 0; i < list.size(); i++) {
                        if (i < stacksHandler.getStacks().getSlots()) {
                            stacksHandler.getStacks().setStackInSlot(i, list.get(i));
                        }
                    }
                }
            });
        });

        keepInventory = false;
        storedItems.clear();
        curiosItems.clear();
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        keepInventory = nbt.getBoolean("KeepInventory");
        storedItems.clear();

        ListTag itemsTag = nbt.getList("StoredItems", Tag.TAG_COMPOUND);
        for (Tag tag : itemsTag) {
            storedItems.add(ItemStack.of((CompoundTag) tag));
        }

        curiosItems.clear();
        if (nbt.contains("CuriosItems", Tag.TAG_COMPOUND)) {
            CompoundTag curiosTag = nbt.getCompound("CuriosItems");
            for (String key : curiosTag.getAllKeys()) {
                ListTag listTag = curiosTag.getList(key, Tag.TAG_COMPOUND);
                List<ItemStack> list = new ArrayList<>();
                for (Tag tag : listTag) {
                    list.add(ItemStack.of((CompoundTag) tag));
                }
                curiosItems.put(key, list);
            }
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

        CompoundTag curiosTag = new CompoundTag();
        for (Map.Entry<String, List<ItemStack>> entry : curiosItems.entrySet()) {
            ListTag list = new ListTag();
            for (ItemStack stack : entry.getValue()) {
                list.add(stack.save(new CompoundTag()));
            }
            curiosTag.put(entry.getKey(), list);
        }
        nbt.put("CuriosItems", curiosTag);
    }
}