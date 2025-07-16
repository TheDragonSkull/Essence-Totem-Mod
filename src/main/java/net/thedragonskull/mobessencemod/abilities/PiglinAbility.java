package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashSet;
import java.util.Set;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class PiglinAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        Set<Item> uniqueGoldItems = getUniqueGoldItems(player);

        int count = uniqueGoldItems.size();
        int luckLevel = count / 5;

        if (luckLevel > 0) {
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 2, luckLevel - 1, true, false, true));
        }
    }

    public static void onPiglinAttack(LivingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:piglin"))) return;

        Set<Item> uniqueGoldItems = getUniqueGoldItems(player);

        int count = uniqueGoldItems.size();
        if (count == 0) return;

        float multiplier = 1.0f + (count * 0.1f);
        float original = event.getAmount();
        event.setAmount(original * multiplier);

        player.displayClientMessage(Component.literal(String.valueOf(event.getAmount())), true);
    }

    private static Set<Item> getUniqueGoldItems(ServerPlayer player) {
        Set<Item> uniqueGoldItems = new HashSet<>();

        // Inventory
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.isEmpty() && GOLD_ITEMS.contains(stack.getItem())) {
                uniqueGoldItems.add(stack.getItem());
            }
        }

        // Offhand
        for (ItemStack stack : player.getInventory().offhand) {
            if (!stack.isEmpty() && GOLD_ITEMS.contains(stack.getItem())) {
                uniqueGoldItems.add(stack.getItem());
            }
        }

        // Armor
        for (ItemStack stack : player.getInventory().armor) {
            if (!stack.isEmpty() && GOLD_ITEMS.contains(stack.getItem())) {
                uniqueGoldItems.add(stack.getItem());
            }
        }

        return uniqueGoldItems;
    }

    static Set<Item> GOLD_ITEMS = Set.of(
            Items.RAW_GOLD,
            Items.GOLD_INGOT,
            Items.GOLD_NUGGET,
            Items.GOLDEN_CARROT,
            Items.GLISTERING_MELON_SLICE,
            Items.CLOCK,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,

            Items.GOLD_BLOCK,
            Items.GOLD_ORE,
            Items.DEEPSLATE_GOLD_ORE,
            Items.NETHER_GOLD_ORE,
            Items.GILDED_BLACKSTONE,
            Items.RAW_GOLD_BLOCK,
            Items.BELL,
            Items.POWERED_RAIL,
            Items.LIGHT_WEIGHTED_PRESSURE_PLATE,

            Items.GOLDEN_SWORD,
            Items.GOLDEN_PICKAXE,
            Items.GOLDEN_AXE,
            Items.GOLDEN_HOE,
            Items.GOLDEN_SHOVEL,
            Items.GOLDEN_HELMET,
            Items.GOLDEN_CHESTPLATE,
            Items.GOLDEN_LEGGINGS,
            Items.GOLDEN_BOOTS,
            Items.GOLDEN_HORSE_ARMOR
    );

}
