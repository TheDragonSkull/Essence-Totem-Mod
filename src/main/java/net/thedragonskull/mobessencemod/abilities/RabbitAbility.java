package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class RabbitAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onRabbitFrenzy(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:rabbit"))) return;

        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.getRandom().nextInt(4) == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 15, 0));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 15, 0));

            player.level().playSound(null, player.blockPosition(), SoundEvents.RABBIT_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    // RabbitJumpMixin
}
