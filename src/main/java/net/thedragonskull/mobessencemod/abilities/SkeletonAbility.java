package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import javax.annotation.Nullable;
import java.util.*;

public class SkeletonAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    private static final Set<UUID> recentFreeArrows = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<UUID, Item> arrowsToReturn = new WeakHashMap<>();

    public static void tryPreventArrowConsumption(ArrowLooseEvent event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:skeleton"))) return;

        ItemStack bow = event.getBow();
        ItemStack usedArrow = player.getProjectile(bow);
        if (usedArrow.isEmpty()) return;

        Item item = usedArrow.getItem();

        boolean isRegular = item == Items.ARROW;
        boolean isSpecial = item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;

        boolean hasInfinity = bow.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
        if (isRegular && hasInfinity) return;

        int chance = isRegular ? 2 : (isSpecial ? 4 : 0);
        if (chance == 0) return;

        if (player.level().getRandom().nextInt(chance) == 0) {
            recentFreeArrows.add(player.getUUID());
            arrowsToReturn.put(player.getUUID(), item);
        }
    }

    public static void tryReturnArrow(ServerPlayer player) {
        UUID id = player.getUUID();
        Item item = arrowsToReturn.remove(id);
        if (item == null) return;

        ItemStack match = findExactArrowStack(player, item);
        if (match != null) {
            match.setCount(match.getCount() + 1);
        }
    }

    private static @Nullable ItemStack findExactArrowStack(ServerPlayer player, Item targetItem) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == targetItem) {
                return stack;
            }
        }
        return null;
    }

    public static boolean markedFreeArrow(Player player) {
        return recentFreeArrows.remove(player.getUUID());
    }
}
