package net.thedragonskull.mobessencemod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.thedragonskull.mobessencemod.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.Optional;

public class TotemUtils {

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


}
