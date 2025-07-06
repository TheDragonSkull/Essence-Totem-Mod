package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class TurtleAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void turtleBlock(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:turtle"))) return;

        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        Vec3 dirToAttacker = attacker.position().subtract(player.position()).normalize();
        Vec3 backDir = player.getLookAngle().normalize().scale(-1);
        double dot = dirToAttacker.dot(backDir);

        if (dot < 0.5) return;

        player.level().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 2.0f, 1.0f);


        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CRIT,
                player.getX(), player.getY() + 1, player.getZ(),
                10, 0.3, 0.5, 0.3, 0.02);

        event.setCanceled(true);
    }
}
