package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class WanderingTraderAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onLlamaTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Llama)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:wandering_trader"))) {
            event.setNewTarget(null);
        }
    }


    public static void onUseEmerald(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer sp)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(sp, ResourceLocation.parse("minecraft:wandering_trader"))) return;

        ItemStack stack = event.getItemStack();
        if (!stack.is(Items.EMERALD)) return;

        event.setCanceled(true);

        player.getCooldowns().addCooldown(Items.EMERALD, 30);
        stack.shrink(1);

        MerchantOffers offers = new MerchantOffers();

        VillagerTrades.WANDERING_TRADER_TRADES.forEach((level, listings) -> {
            for (VillagerTrades.ItemListing listing : listings) {
                MerchantOffer offer = listing.getOffer(sp, sp.getRandom());
                if (offer != null) {
                    offers.add(offer);
                }
            }
        });

        if (offers.isEmpty()) return;

        MerchantOffer offer = offers.get(sp.getRandom().nextInt(offers.size()));
        ItemStack reward = offer.getResult().copy();

        sp.getInventory().placeItemBackInInventory(reward);

        player.level().playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
