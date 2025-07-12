package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class WitchAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void witchPotion(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:witch"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypeTags.WITCH_RESISTANT_TO)) {
            float reduced = event.getAmount() * 0F;
            event.setAmount(reduced);
        }

        RandomSource random = player.getRandom();
        if (source.is(DamageTypeTags.IS_DROWNING)) {
            if (random.nextInt(20) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * 20, 0));
                player.level().playSound(null, player.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.literal("You find a potion in your pocket..."), true);
            }

        } else if (source.is(DamageTypeTags.IS_FIRE)) {
            if (random.nextInt(25) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 20, 0));
                player.level().playSound(null, player.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.displayClientMessage(Component.literal("You find a potion in your pocket..."), true);
            }

        } else if (player.getHealth() <= player.getMaxHealth() * 0.5F) {
            if (source.is(DamageTypeTags.IS_PROJECTILE) || source.getDirectEntity() instanceof AbstractArrow) {
                if (random.nextInt(6) == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 0));
                    player.level().playSound(null, player.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.literal("You find a potion in your pocket..."), true);
                }

            } else if (source.getEntity() instanceof LivingEntity && !source.is(DamageTypeTags.IS_PROJECTILE)) {
                if (random.nextInt(10) == 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 1));
                    player.level().playSound(null, player.blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.literal("You find a potion in your pocket..."), true);
                }
            }
        }
    }

}
