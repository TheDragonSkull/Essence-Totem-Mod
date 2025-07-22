package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.block.ModBlocks;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class EnderDragonAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onDragonHeal(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.player;
        Level level = player.level();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon"))) return;

        BlockPos playerPos = player.blockPosition();
        int radius = 5;
        boolean foundAny = false;

        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(Blocks.END_ROD)) {
                BlockState newState = ModBlocks.HEALING_END_ROD.get()
                        .defaultBlockState()
                        .setValue(EndRodBlock.FACING, blockState.getValue(EndRodBlock.FACING));

                level.setBlock(pos, newState, 3);
                foundAny = true;
            }
        }

        if (!foundAny) return;

        if (player.tickCount % 20 == 0) {
            player.heal(0.5f);
        }
    }
}
