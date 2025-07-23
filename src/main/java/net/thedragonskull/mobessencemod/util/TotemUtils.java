package net.thedragonskull.mobessencemod.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.item.custom.TotemOfEssenceItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
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

    public static void setEssence(ItemStack stack, ResourceLocation essenceId) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("Essence", essenceId.toString());
    }

    public static CompoundTag makeEssenceTag(ResourceLocation essenceId) {
        CompoundTag tag = new CompoundTag();
        tag.putString("Essence", essenceId.toString());
        return tag;
    }

    public static boolean hasAdvancement(ServerPlayer player, String id) {
        Advancement adv = player.server.getAdvancements().getAdvancement(ResourceLocation.parse(id));
        return adv != null && player.getAdvancements().getOrStartProgress(adv).isDone();
    }

    public static void failMessage(Player player, String msg) {
        player.displayClientMessage(Component.literal(msg).withStyle(ChatFormatting.RED), true);
    }

    public static String formatMobName(String rawId) {
        return Arrays.stream(rawId.split("_"))
                .map(word -> word.isEmpty()
                        ? word
                        : Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .reduce((a, b) -> a + " " + b)
                .orElse(rawId);
    }

    public static void onDragonEggUse(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        if (level.isClientSide || !(player instanceof ServerPlayer)) return;
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (!stack.is(ModItems.TOTEM_OF_ESSENCE.get())) return;

        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.DRAGON_EGG)) return;
        if (player.isShiftKeyDown()) return;

        ResourceLocation dragonId = ResourceLocation.parse("minecraft:ender_dragon");

        if (TotemUtils.hasEssence(stack)) {
            ResourceLocation currentEssence = TotemUtils.getEssence(stack);
            if (dragonId.equals(currentEssence)) {
                player.displayClientMessage(Component.literal("This totem already contains the essence of the Ender Dragon")
                        .withStyle(ChatFormatting.GRAY), true);
                return;
            }
        }

        TotemUtils.setEssence(stack, dragonId);
        player.getCooldowns().addCooldown(stack.getItem(), 20);

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

        ((ServerLevel) level).sendParticles(
                ParticleTypes.DRAGON_BREATH,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                30,
                0.1, 0.1, 0.1,
                .1
        );

        ((ServerLevel) level).sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                30,
                0.1, 0.1, 0.1,
                1
        );

        level.playSound(null, pos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.2f, 1.0f);
        level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.5f, 0.8f);

        player.displayClientMessage(Component.literal("The essence of the End has been absorbed")
                .withStyle(ChatFormatting.LIGHT_PURPLE), true);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
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

    public static void restoreIfPressed(KeyMapping mapping, long window) {
        if (InputConstants.isKeyDown(window, mapping.getKey().getValue())) {
            mapping.setDown(true);
        }
    }

    public static void onMobDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getPersistentData().getBoolean("MobEssenceSummoned")) {
            event.setCanceled(true);
        }
    }

}
