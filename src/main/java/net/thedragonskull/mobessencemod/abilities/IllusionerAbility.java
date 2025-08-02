package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class IllusionerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        Level level = player.level();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:illusioner"))) return;
        if (!player.isSprinting()) return;
        if (player.isCrouching()) return;
        if (player.isInWater()) return;

        BlockPos below = BlockPos.containing(player.getX(), player.getY() - 0.1, player.getZ());
        BlockState belowState = level.getBlockState(below);
        if (!belowState.getFluidState().is(Fluids.WATER)) return;

        double speed = player.getDeltaMovement().horizontalDistanceSqr();
        double splashPower = Mth.clamp(speed * 20.0, 0.1, 2.0);
        double particleCount = Mth.clamp(player.getSpeed() * 20.0, 1, 10.0);

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 0.8;
            double offsetZ = (Math.random() - 0.5) * 0.8;

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SPLASH,
                    player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05 * splashPower);

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.BUBBLE_POP,
                    player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05 * splashPower);
        }

        if (player.tickCount % 10 == 0) {
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.BOAT_PADDLE_WATER,
                    SoundSource.BLOCKS,
                    1.0F,
                    6.0F
            );
        }
    }


}
