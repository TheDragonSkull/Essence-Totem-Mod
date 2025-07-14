package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class SnifferAbility implements IMobAbility {

    Map<Item, List<Block>> SCENT_MAP = Map.ofEntries(
            Map.entry(Items.COAL, List.of(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)),
            Map.entry(Items.COPPER_INGOT, List.of(Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE)),
            Map.entry(Items.IRON_INGOT, List.of(Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE)),
            Map.entry(Items.GOLD_INGOT, List.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.NETHER_GOLD_ORE)),
            Map.entry(Items.REDSTONE, List.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE)),
            Map.entry(Items.AMETHYST_SHARD, List.of(Blocks.BUDDING_AMETHYST, Blocks.AMETHYST_BLOCK)),
            Map.entry(Items.EMERALD, List.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE)),
            Map.entry(Items.DIAMOND, List.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE)),
            Map.entry(Items.LAPIS_LAZULI, List.of(Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE)),
            Map.entry(Items.QUARTZ, List.of(Blocks.NETHER_QUARTZ_ORE)),
            Map.entry(Items.NETHERITE_INGOT, List.of(Blocks.ANCIENT_DEBRIS)),
            Map.entry(Items.FLINT, List.of(Blocks.SUSPICIOUS_GRAVEL, Blocks.GRAVEL))
    );

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        ItemStack offhand = player.getOffhandItem();

        if (offhand.isEmpty()) return;
        List<Block> targetBlocks = SCENT_MAP.get(offhand.getItem());
        if (targetBlocks == null) return;

        BlockPos playerPos = player.blockPosition();
        int radius = 30;
        BlockPos found = null;

        outer:
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    Block block = player.level().getBlockState(pos).getBlock();
                    if (targetBlocks.contains(block)) {
                        found = pos;
                        break outer;
                    }
                }
            }
        }

        if (found != null) {
            double dist = player.position().distanceTo(Vec3.atCenterOf(found));

            if (dist <= 30) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 1, true, false, true));
            }

            if (player.tickCount % 100 == 0) {
                SoundEvent sound;
                if (dist > 20) {
                    sound = SoundEvents.SNIFFER_SEARCHING;
                } else if (dist > 10) {
                    sound = SoundEvents.SNIFFER_SCENTING;
                } else {
                    sound = SoundEvents.SNIFFER_SNIFFING;
                }

                player.level().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, 10.0f, 1.0F);
            }

        }
    }
}
