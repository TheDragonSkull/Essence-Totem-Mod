package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class AxolotlAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerKill(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity killer = source.getEntity();

        if (!(killer instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:axolotl"))) return;

        if (event.getEntity() instanceof Mob mob) {
            LivingEntity target = mob.getTarget();

            if (target == player) {
                int durationPerKill = 100;
                int amplifier = player.isUnderWater() ? 1 : 0;

                MobEffectInstance current = player.getEffect(MobEffects.REGENERATION);
                int newDuration = durationPerKill;

                if (current != null) {
                    newDuration += current.getDuration();
                }

                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, newDuration, amplifier));
                player.level().playSound(null, player.blockPosition(), SoundEvents.AXOLOTL_ATTACK, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }



}
