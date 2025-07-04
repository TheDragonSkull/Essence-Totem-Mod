package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class DonkeyAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void donkeyKick(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:donkey"))) return;

        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        Vec3 dirToAttacker = attacker.position().subtract(player.position()).normalize();
        Vec3 backDir = player.getLookAngle().normalize().scale(-1);
        double dot = dirToAttacker.dot(backDir);

        if (dot < 0.5) return;

        if (player.getRandom().nextInt(3) != 0) return;

        Vec3 knockback = backDir.normalize().scale(1.5);
        attacker.setDeltaMovement(attacker.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
        attacker.hurt(attacker.level().damageSources().cactus(), 2);
        attacker.hurtMarked = true;

        player.level().playSound(null, player.blockPosition(), SoundEvents.HORSE_LAND, SoundSource.PLAYERS, 1.0f, 1.0f);

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 1, player.getZ(),
                15, 0.5, 0.5, 0.5, 0.1);

        BlockPos under = player.blockPosition().below();
        BlockState blockState = player.level().getBlockState(under);

        for (int i = 0; i < 30; i++) {
            ((ServerLevel) player.level()).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    player.getX(), player.getY(), player.getZ(),
                    5,
                    0.25, 0.25, 0.25,
                    0.05
            );
        }
    }


}
