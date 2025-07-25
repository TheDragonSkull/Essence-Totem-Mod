package net.thedragonskull.mobessencemod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.thedragonskull.mobessencemod.abilities.TotemEssenceRegistry;
import net.thedragonskull.mobessencemod.util.TotemMobCategory;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static net.thedragonskull.mobessencemod.util.TotemUtils.failMessage;
import static net.thedragonskull.mobessencemod.util.TotemUtils.hasAdvancement;

public class TotemOfEssenceItem extends Item implements ICurioItem {

    public TotemOfEssenceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        ResourceLocation mobId;

        if (target instanceof MushroomCow mooshroom) {
            MushroomCow.MushroomType cowVariant = mooshroom.getVariant();

            mobId = switch (cowVariant) {
                case RED -> ResourceLocation.parse("minecraft:red_mooshroom");
                case BROWN -> ResourceLocation.parse("minecraft:brown_mooshroom");
            };

        } else if (target instanceof Fox fox) {
            Fox.Type foxVariant = fox.getVariant();

            mobId = switch (foxVariant) {
                case RED -> ResourceLocation.parse("minecraft:fox");
                case SNOW -> ResourceLocation.parse("minecraft:snow_fox");
            };
        } else if (target instanceof Frog frog) {
            FrogVariant frogVariant = frog.getVariant();

            if (frogVariant == FrogVariant.TEMPERATE) {
                mobId = ResourceLocation.parse("minecraft:temperate_frog");
            } else if (frogVariant == FrogVariant.WARM) {
                mobId = ResourceLocation.parse("minecraft:warm_frog");
            } else if (frogVariant == FrogVariant.COLD) {
                mobId = ResourceLocation.parse("minecraft:cold_frog");
            } else {
                mobId = ResourceLocation.parse("minecraft:frog");
            }
        } else {
            mobId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());
        }

        if (player.level().isClientSide() || !(player instanceof ServerPlayer serverPlayer))
            return InteractionResult.PASS;

        if (target instanceof Player) {
            if (!hasAdvancement(serverPlayer, "minecraft:end/enter_end_gateway")) {
                failMessage(player, "You must beat the game for the first time to capture his essence!");
                return InteractionResult.PASS;
            }
        } else if (target instanceof WitherBoss) {
            if (!player.getPersistentData().getBoolean("mobessence_killed_wither")) {
                failMessage(player, "You must kill the Wither for the first time to capture his essence!");
                return InteractionResult.PASS;
            }
        }

        if (mobId == null || !TotemEssenceRegistry.isRegistered(mobId) || player.isShiftKeyDown()) {
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

            player.displayClientMessage(Component.literal("Stored essence of " + TotemUtils.formatMobName(mobId.getPath())), true);

            TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(mobId);
            if (data != null) {
                player.level().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        data.sound(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );
            }

            if (target instanceof ArmorStand) return InteractionResult.SUCCESS;

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
                if (!TotemEssenceRegistry.isRegistered(ResourceLocation.parse(essenceId))) {
                    player.displayClientMessage(Component.literal("This essence is invalid").withStyle(ChatFormatting.RED), true);
                    return InteractionResultHolder.pass(stack);
                }

                stack.getTag().remove("Essence");
                player.setItemInHand(hand, stack);
                player.getCooldowns().addCooldown(this, 20);

                if (!essenceId.equals("minecraft:armor_stand")) {
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
                } else {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_STAND_BREAK, SoundSource.PLAYERS, 10.0F, 1.0F);
                }

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
            tooltip.add(Component.literal("[" + data.category().toString() + "] ").withStyle(data.category().asStyle())
                    .append(Component.literal(data.tooltip().description).withStyle(ChatFormatting.AQUA)));

        } else {
            tooltip.add(Component.literal("Press Shift for details").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public Component getName(ItemStack stack) {
        ResourceLocation essence = TotemUtils.getEssence(stack);

        if (essence != null && TotemEssenceRegistry.isRegistered(essence)) {
            TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(essence);

            return Component.translatable(this.getDescriptionId(stack)).withStyle(data.category().asStyle());
        }

        return super.getName(stack);
    }


    @Override
    public String getDescriptionId(ItemStack stack) {

        ResourceLocation essence = TotemUtils.getEssence(stack);
        if (essence == null || !TotemEssenceRegistry.isRegistered(essence)) {
            return super.getDescriptionId();
        }
        return super.getDescriptionId() + "." + essence.getPath();
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        ResourceLocation essence = TotemUtils.getEssence(pStack);

        if (essence != null && TotemEssenceRegistry.isRegistered(essence)) {
            TotemEssenceRegistry.EssenceData data = TotemEssenceRegistry.get(essence);

            return data.category() == TotemMobCategory.SPECIAL || data.category() == TotemMobCategory.NON_MOB || data.category() == TotemMobCategory.BOSS;
        }

        return false;
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
