package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Set;

public class PigAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
    }

    @SubscribeEvent
    public static void onItemEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack eaten = event.getItem();

        if (!eaten.isEdible()) return;
        if (!TotemUtils.hasTotemWithEssence(player, ResourceLocation.parse("minecraft:pig"))) return;

        Item item = eaten.getItem();
        if (isBadFood(item)) {
            removeNegativeEffects(player);
        }
    }

    private static boolean isBadFood(Item item) {
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

    private static void removeNegativeEffects(Player player) {
        List<MobEffect> toRemove = List.of(
                MobEffects.HUNGER,
                MobEffects.POISON,
                MobEffects.CONFUSION,
                MobEffects.WEAKNESS
        );

        for (MobEffect effect : toRemove) {
            player.removeEffect(effect);
        }
    }

}
