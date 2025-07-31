package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.Optional;

public class PigAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
    }

    public static void onItemEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack eaten = event.getItem();

        if (!eaten.isEdible()) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:pig"))) return;

        Item item = eaten.getItem();
        if (TotemUtils.isBadFood(item)) {
            removeNegativeEffects(player);
        }
    }

    public static void onTotemTransform(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation pigId = ResourceLocation.parse("minecraft:pig");
        ResourceLocation zombifiedId = ResourceLocation.parse("minecraft:zombified_piglin");

        Optional<SlotResult> slotOpt = TotemUtils.findTotemWithEssenceServer(player, pigId);
        if (slotOpt.isEmpty()) return;

        ItemStack newStack = new ItemStack(ModItems.TOTEM_OF_ESSENCE.get());
        TotemUtils.setEssence(newStack, zombifiedId);

        SlotResult result = slotOpt.get();
        String slotId = result.slotContext().identifier();
        int index = result.slotContext().index();

        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            inv.setEquippedCurio(slotId, index, newStack);
        });

        ServerLevel level = (ServerLevel) player.level();
        level.playSound(null, player.blockPosition(), SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundSource.PLAYERS, 1.5f, 0.9f);
        level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5f, 0.9f);
        level.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.5, 0.4, 0.01);

        player.displayClientMessage(Component.literal("Your totem trembles as it twists into something... darker")
                .withStyle(ChatFormatting.DARK_PURPLE), true);

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
