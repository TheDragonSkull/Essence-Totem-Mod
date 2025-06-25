package net.thedragonskull.mobessencemod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.thedragonskull.mobessencemod.abilities.IMobAbility;
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class TotemOfEssenceItem extends Item implements ICurioItem {

    public TotemOfEssenceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());

        if (mobId == null || !TotemEssenceRegistry.isRegistered(mobId)) {
            player.displayClientMessage(Component.literal("This creature has no essence to offer.").withStyle(ChatFormatting.RED), true);
            return InteractionResult.PASS;
        }

        if (!player.getCooldowns().isOnCooldown(this)) {
            if (stack.hasTag() && stack.getTag().contains("Essence")) {
                String currentEssence = stack.getTag().getString("Essence");
                if (currentEssence.equals(mobId.toString())) {
                    player.displayClientMessage(Component.literal("This totem already contains the essence of " + mobId.getPath()).withStyle(ChatFormatting.GRAY), true);
                    return InteractionResult.PASS;
                }
            }

            ItemStack copy = stack.copy();
            copy.getOrCreateTag().putString("Essence", mobId.toString());
            player.setItemInHand(hand, copy);
            player.getCooldowns().addCooldown(this, 20);

            player.displayClientMessage(Component.literal("Stored essence of " + mobId.getPath()), true);

            TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(mobId);
            if (data != null) {
                player.playSound(data.sound(), 1.0F, 1.0F);
            }

            for (int i = 0; i < 3; i++) {
                player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
            }

            if (!player.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) player.level();
                double x = target.getX();
                double y = target.getY() + target.getBbHeight() / 2.0;
                double z = target.getZ();

                serverLevel.sendParticles(
                        ParticleTypes.SOUL,
                        x, y, z,
                        20,
                        0.3, 0.5, 0.3,
                        0.01
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player.isShiftKeyDown()) {
            if (stack.hasTag() && stack.getTag().contains("Essence")) {

                String essenceId = stack.getTag().getString("Essence");
                if (!TotemEssenceRegistry.isRegistered(new ResourceLocation(essenceId))) {
                    player.displayClientMessage(Component.literal("This essence is invalid").withStyle(ChatFormatting.RED), true);
                    return InteractionResultHolder.pass(stack);
                }

                stack.getTag().remove("Essence");
                player.setItemInHand(hand, stack);
                player.getCooldowns().addCooldown(this, 20);

                for (int i = 0; i < 3; i++) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
                }

                ServerLevel serverLevel = (ServerLevel) player.level();
                double x = player.getX();
                double y = player.getY() + player.getBbHeight() / 2.0;
                double z = player.getZ();

                serverLevel.sendParticles(
                        ParticleTypes.SOUL,
                        x, y, z,
                        20,
                        0.3, 0.5, 0.3,
                        0.01
                );

                player.displayClientMessage(Component.literal("Essence removed"), true);
                return InteractionResultHolder.success(stack);

            } else {
                ResourceLocation essence = TotemUtils.getEssence(stack);
                if (essence == null || !TotemEssenceRegistry.isRegistered(essence)) {
                    player.displayClientMessage(Component.literal("No valid essence to remove").withStyle(ChatFormatting.GRAY), true);
                    return InteractionResultHolder.pass(stack);
                }

            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation essence = TotemUtils.getEssence(stack);
        if (essence == null) return;

        TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(essence);

        if (data != null && Screen.hasShiftDown()) {
            tooltip.add(Component.literal(data.tooltip().title + ":").withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.literal(data.tooltip().description).withStyle(ChatFormatting.AQUA));

        } else {
            tooltip.add(Component.literal("Press Shift for details").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public String getDescriptionId(ItemStack stack) {

        ResourceLocation essence = TotemUtils.getEssence(stack);
        if (essence == null || !TotemEssenceRegistry.isRegistered(essence)) {
            return super.getDescriptionId();
        }
        return super.getDescriptionId() + "." + essence.getPath();
    }

    // CURIOS THINGS

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer serverPlayer)) return;

        ResourceLocation essence = TotemUtils.getEssence(stack);
        if (essence == null) return;

        TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(essence);
        if (data != null && data.ability() != null) {
            data.ability().tick(serverPlayer, stack);
        }
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

}
