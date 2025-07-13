package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class FrogAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onFrogFallDamage(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:temperate_frog")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warm_frog")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cold_frog"))))
            return;

        if (event.getDistance() <= 5.0F) {
            event.setCanceled(true);
        }
    }

    public static void onFrogJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:temperate_frog")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warm_frog")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cold_frog"))))
            return;

        if (!player.isSprinting()) {
            player.push(0, 0.4, 0);
            player.hurtMarked = true;

            player.level().playSound(null, player.blockPosition(), SoundEvents.FROG_LONG_JUMP, SoundSource.PLAYERS, 2.0f, 1.0f);
        }
    }

    public static void onFrogImmunity(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();

        if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cold_frog")) &&
                source.is(DamageTypes.FREEZE)) {
            event.setCanceled(true);
        }

        if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:temperate_frog")) &&
                source.getMsgId().equals("magic") &&
                player.hasEffect(MobEffects.POISON)) {

            event.setCanceled(true);
        }
    }

    // CancelEffectMixin.mobessence$preventSlownessIfWarmFrog

}
