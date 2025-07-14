package net.thedragonskull.mobessencemod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thedragonskull.mobessencemod.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public class TotemUtils {

    // TOTEM RELATED
    public static @Nullable ItemStack getVisibleTotemStack(Player player) {
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, stack -> stack.getItem() == ModItems.TOTEM_OF_ESSENCE.get())
                .filter(result -> result.slotContext().visible())
                .map(SlotResult::stack)
                .orElse(null);
    }

    public static @Nullable ResourceLocation getEssence(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains("Essence")) return null;

        try {
            return ResourceLocation.parse(stack.getTag().getString("Essence"));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean hasEssence(ItemStack stack) {
        return getEssence(stack) != null;
    }

    // CLIENT
    public static Optional<SlotResult> findTotemWithEssenceClient(Player player, ResourceLocation essenceId) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> {
            if (stack.getItem() != ModItems.TOTEM_OF_ESSENCE.get()) return false;
            ResourceLocation essence = getEssence(stack);
            return essenceId.equals(essence);
        });
    }

    public static boolean hasTotemWithEssenceClient(Player player, ResourceLocation essenceId) {
        return findTotemWithEssenceClient(player, essenceId).isPresent();
    }

    // SERVER
    public static Optional<SlotResult> findTotemWithEssenceServer(ServerPlayer player, ResourceLocation essenceId) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> {
            if (stack.getItem() != ModItems.TOTEM_OF_ESSENCE.get()) return false;
            ResourceLocation essence = getEssence(stack);
            return essenceId.equals(essence);
        });
    }

    public static boolean hasTotemWithEssenceServer(ServerPlayer player, ResourceLocation essenceId) {
        return findTotemWithEssenceServer(player, essenceId).isPresent();
    }

    public static void swapEssenceTotemServer(ServerPlayer player) {
        ItemStack hand = player.getMainHandItem();
        boolean handIsTotem = hand.getItem() == ModItems.TOTEM_OF_ESSENCE.get();
        boolean handIsEmpty = hand.isEmpty();

        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            Optional<SlotResult> equipped = inv.findFirstCurio(stack -> stack.getItem() == ModItems.TOTEM_OF_ESSENCE.get());

            if (equipped.isPresent()) {

                if (!handIsTotem && !handIsEmpty) return;

                SlotResult result = equipped.get();
                int slotIndex = result.slotContext().index();
                String slotId = result.slotContext().identifier();
                ItemStack curiosStack = result.stack();

                inv.setEquippedCurio(slotId, slotIndex, hand.copy());
                player.setItemInHand(InteractionHand.MAIN_HAND, curiosStack.copy());
                player.swing(InteractionHand.MAIN_HAND);

            } else if (handIsTotem) {
                ICurioStacksHandler handler = inv.getCurios().get("totem_of_essence");
                if (handler == null) return;

                IDynamicStackHandler stacks = handler.getStacks();

                if (stacks.getStackInSlot(0).isEmpty()) {
                    stacks.setStackInSlot(0, hand.copy());
                    player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                    player.swing(InteractionHand.MAIN_HAND);
                }
            }
        });
    }

    // ABILITY RELATED
    public static boolean isBadFood(Item item) {
        return Set.of(
                Items.ROTTEN_FLESH,
                Items.SPIDER_EYE,
                Items.PUFFERFISH,
                Items.POISONOUS_POTATO,
                Items.CHICKEN,
                Items.PORKCHOP,
                Items.MUTTON,
                Items.BEEF,
                Items.RABBIT,
                Items.COD,
                Items.SALMON
        ).contains(item);
    }

    public static boolean isVegetable(Item item) {
        return Set.of(
                Items.POTATO,
                Items.BAKED_POTATO,
                Items.BEETROOT,
                Items.BEETROOT_SOUP,
                Items.CARROT,
                Items.GOLDEN_CARROT,
                Items.DRIED_KELP
        ).contains(item);
    }


}
