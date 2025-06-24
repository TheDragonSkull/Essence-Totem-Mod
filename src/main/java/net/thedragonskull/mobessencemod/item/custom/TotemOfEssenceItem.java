package net.thedragonskull.mobessencemod.item.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class TotemOfEssenceItem extends Item {

    public TotemOfEssenceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {

        ResourceLocation mobId = ForgeRegistries.ENTITY_TYPES.getKey(target.getType());

        if (mobId != null && !player.getCooldowns().isOnCooldown(this)) {
            ItemStack copy = stack.copy();
            copy.getOrCreateTag().putString("Essence", mobId.toString());

            player.setItemInHand(hand, copy);
            player.getCooldowns().addCooldown(this, 20);
            player.displayClientMessage(Component.literal("Stored essence of " + mobId.getPath()), true);

            System.out.println(mobId);
            SoundEvent sound = MobEssenceSounds.getSoundForMob(mobId);
            player.playSound(sound, 1.0F, 1.0F);

            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);

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
                stack.getTag().remove("Essence");
                player.setItemInHand(hand, stack);

                player.getCooldowns().addCooldown(this, 20);

                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 10.0F, 1.0F);

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
            } else {
                player.displayClientMessage(Component.literal("No essence to remove"), true);
            }

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    private static class MobEssenceSounds {

        public static final Map<String, SoundEvent> MOB_SOUNDS = Map.ofEntries(
                Map.entry("minecraft:pig", SoundEvents.PIG_HURT),
                Map.entry("minecraft:bee", SoundEvents.BEE_HURT)
        );

        public static SoundEvent getSoundForMob(ResourceLocation mobId) {
            return MOB_SOUNDS.getOrDefault(mobId.toString(), SoundEvents.EXPERIENCE_ORB_PICKUP);
        }
    }
}
