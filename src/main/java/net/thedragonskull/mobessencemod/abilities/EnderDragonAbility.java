package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.block.ModBlocks;
import net.thedragonskull.mobessencemod.network.C2SFlapSoundPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnderDragonAbility implements IMobAbility {

    private static final Map<UUID, Integer> flapCount = new HashMap<>();
    private static boolean wasJumpKeyDown = false;

    private static final int MAX_FLAPS = 5;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        if (player.onGround() || player.isInWater() || player.isInLava() || player.isSwimming()) {
            flapCount.remove(player.getUUID());
        }
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

    public static void flap(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:ender_dragon")))
            return;

        UUID uuid = player.getUUID();
        int currentFlaps = flapCount.getOrDefault(uuid, 0);
        boolean jumpKeyDown = mc.options.keyJump.isDown();

        if (jumpKeyDown && !wasJumpKeyDown) {
            boolean isFlying = player.getAbilities().flying;
            boolean isEligible = !player.onGround() && !isFlying && !player.isInWater() && !player.isInLava() && !player.isSwimming();

            if (isEligible && currentFlaps < MAX_FLAPS) {
                Vec3 motion = player.getDeltaMovement();
                double verticalBoost = 0.45 + (0.02 * currentFlaps);
                player.setDeltaMovement(motion.x, verticalBoost, motion.z);
                player.hasImpulse = true;

                PacketHandler.sendToServer(new C2SFlapSoundPacket(ResourceLocation.parse("minecraft:ender_dragon")));

                flapCount.put(uuid, currentFlaps + 1);
            }
        }

        wasJumpKeyDown = jumpKeyDown;
    }
}
