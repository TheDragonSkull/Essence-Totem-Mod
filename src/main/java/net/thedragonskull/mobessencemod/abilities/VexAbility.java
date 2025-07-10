package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class VexAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onTraverseBlock(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (event.player.level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) event.player;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:vex"))) return;

        if (player.isSprinting() && !player.isCrouching()) {

            Direction dir = player.getDirection();
            BlockPos frontPos = player.blockPosition().relative(dir);
            if (!player.level().getBlockState(frontPos).isAir()) {

                BlockPos start = player.blockPosition();

                for (int i = 1; i <= 2; i++) {
                    BlockPos targetPos = start.relative(dir, i);
                    BlockPos headPos = targetPos.above();

                    if (player.level().getBlockState(targetPos).isAir() && player.level().getBlockState(headPos).isAir()) {
                        double targetX = targetPos.getX() + 0.5;
                        double targetY = targetPos.getY();
                        double targetZ = targetPos.getZ() + 0.5;
                        player.teleportTo(targetX, targetY, targetZ);

                        player.level().playSound(null, targetX, targetY, targetZ, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
                        break;
                    }
                }
            }
        }
    }

    public static void onVexAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:vex"))) return;

        if (player.isSprinting() && event.getTarget() instanceof LivingEntity target) {
            if (player.getRandom().nextInt(3) == 0) {
                float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float totalDamage = baseDamage * 1.5F;

                target.hurt(player.damageSources().playerAttack(player), totalDamage);

                event.setCanceled(true);

                System.out.println(event);

                ((ServerLevel)player.level()).sendParticles(
                        ParticleTypes.CRIT,
                        target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                        25,
                        0.5, 0.5, 0.5,
                        0.2
                );

                player.level().playSound(null, target.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }



}
