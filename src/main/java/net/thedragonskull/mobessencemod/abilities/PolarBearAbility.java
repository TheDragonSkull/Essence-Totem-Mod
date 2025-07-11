package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.util.ModTags;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class PolarBearAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void bearResistance(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:polar_bear"))) return;

        if (player.level().getBiome(player.blockPosition()).is(ModTags.MobEssenceBiomeTags.IS_SNOWY)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2, 0, false, false, false));
        }
    }

    public static void bearKnockback(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof Player)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:polar_bear"))) return;

        LivingEntity target = event.getEntity();

        if (player.getRandom().nextInt(4) == 0) {
            double dx = player.getX() - target.getX();
            double dz = player.getZ() - target.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist > 0) {
                double strength = 1.5;
                dx /= dist;
                dz /= dist;
                target.knockback(strength, dx, dz);
            }

            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.POLAR_BEAR_WARNING, SoundSource.PLAYERS, 1.0F, 1.0F);

            BlockPos under = player.blockPosition().below();
            BlockState blockState = player.level().getBlockState(under);

            for (int i = 0; i < 30; i++) {
                ((ServerLevel) player.level()).sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                        player.getX(), player.getY(), player.getZ(),
                        5,
                        0.5, 0.5, 0.5,
                        0.05
                );
            }
        }
    }

    // CommonAbilityUtils.onFreezingHurt
}
