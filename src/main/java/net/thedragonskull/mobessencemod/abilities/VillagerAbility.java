package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.thedragonskull.mobessencemod.sound.ModSounds;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class VillagerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();
        int radius = 20;
        double closestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(Blocks.EMERALD_ORE) || blockState.is(Blocks.DEEPSLATE_EMERALD_ORE) || blockState.is(Blocks.EMERALD_BLOCK)) {
                double distance = playerPos.distSqr(pos);

                if (distance < closestDistance) {
                    closestDistance = distance;
                }
            }
        }

        if (closestDistance == Double.MAX_VALUE) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:villager"))) return;

        CompoundTag data = player.getPersistentData();
        int soundTimer = data.getInt("mobessence_villager_sound_timer");

        if (soundTimer > 0) {
            data.putInt("mobessence_villager_sound_timer", soundTimer - 1);
            return;
        }

        data.putInt("mobessence_villager_sound_timer", 40);

        float volume = 1.0F;
        float pitch = 1.0F + (float) ((1.0F - Math.sqrt(closestDistance) / radius) * 0.4F);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.VILLAGER_MONEY.get(), SoundSource.PLAYERS, volume, pitch);
    }
}
