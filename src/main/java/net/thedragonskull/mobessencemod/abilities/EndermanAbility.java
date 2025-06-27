package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class EndermanAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceLocation id = ResourceLocation.parse("minecraft:enderman");
        if (!TotemUtils.hasTotemWithEssenceServer(player, id)) return;

        DamageSource source = event.getSource();

        boolean isProjectile = source.is(DamageTypeTags.IS_PROJECTILE);

        if (isProjectile) {
            boolean success = tryRandomTeleport(player);
            if (success) {
                event.setCanceled(true);
                player.invulnerableTime = 20;
            }

        }
    }

    public static boolean tryRandomTeleport(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        RandomSource random = player.getRandom();

        for (int i = 0; i < 32; i++) {
            double dx = player.getX() + (random.nextDouble() - 0.5D) * 32.0D;
            double dy = player.getY() + random.nextInt(16) - 8;
            double dz = player.getZ() + (random.nextDouble() - 0.5D) * 32.0D;

            BlockPos pos = BlockPos.containing(dx, dy, dz);
            BlockState below = level.getBlockState(pos.below());
            BlockState at = level.getBlockState(pos);
            BlockState above = level.getBlockState(pos.above());

            boolean safe = below.blocksMotion() && !at.blocksMotion() && above.isAir();

            if (safe) {
                teleport(player, dx, dy, dz);
                return true;
            }
        }

        // Fallback teleport
        Vec3 fallback = player.position().add(
                (random.nextDouble() - 0.5) * 6.0,
                1.0,
                (random.nextDouble() - 0.5) * 6.0
        );

        BlockPos pos = BlockPos.containing(fallback);
        BlockState below = level.getBlockState(pos.below());
        BlockState at = level.getBlockState(pos);
        BlockState above = level.getBlockState(pos.above());

        if (below.blocksMotion() && !at.blocksMotion() && above.isAir()) {
            teleport(player, fallback.x, fallback.y, fallback.z);
            return true;
        }

        teleport(player, player.getX(), player.getY(), player.getZ());
        return true;
    }

    private static void teleport(ServerPlayer player, double x, double y, double z) {
        ServerLevel level = player.serverLevel();

        double fromX = player.getX();
        double fromY = player.getY();
        double fromZ = player.getZ();

        level.sendParticles(ParticleTypes.REVERSE_PORTAL, fromX, fromY + 1.0, fromZ,
                20, 0.5, 0.5, 0.5, 0.1);

        player.teleportTo(x, y, z);
        player.resetFallDistance();

        level.playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

        level.sendParticles(ParticleTypes.PORTAL, x, y + 1.0, z,
                20, 0.5, 0.5, 0.5, 0.1);
    }



}
