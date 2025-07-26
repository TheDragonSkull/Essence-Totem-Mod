package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

public class AllayAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        if (!player.getOffhandItem().isEmpty()) {
            ItemStack filter = player.getOffhandItem();
            int pickupRadius = 15;

            List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class,
                    player.getBoundingBox().inflate(pickupRadius),
                    item -> !item.hasPickUpDelay() && item.isAlive()
            );

            for (ItemEntity item : items) {
                if (ItemStack.isSameItemSameTags(item.getItem(), filter)) {
                    if (player.addItem(item.getItem())) {
                        item.discard();
                    }
                }
            }
        }

        int jukeboxRadius = 20;
        BlockPos playerPos = player.blockPosition();

        boolean nearJukebox = BlockPos.betweenClosedStream(
                        playerPos.offset(-jukeboxRadius, -jukeboxRadius, -jukeboxRadius),
                        playerPos.offset(jukeboxRadius, jukeboxRadius, jukeboxRadius))
                .map(BlockPos::immutable)
                .anyMatch(pos -> {
                    BlockState state = player.level().getBlockState(pos);
                    return state.getBlock() instanceof JukeboxBlock &&
                            Boolean.TRUE.equals(state.getValue(JukeboxBlock.HAS_RECORD));
                });

        if (nearJukebox) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 1, true, false, false));
        }
    }

    public static void onNoteblockUsed(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:allay"))) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);

        if (state.getBlock() instanceof NoteBlock) {
            List<ItemEntity> items = event.getLevel().getEntitiesOfClass(ItemEntity.class,
                    new AABB(pos).inflate(15));

            boolean movedAny = false;

            for (ItemEntity item : items) {
                ItemStack stack = item.getItem();
                BlockPos targetPos = pos.above();

                if (event.getLevel().getBlockState(targetPos).isAir()) {
                    spawnItem(event.getLevel(), targetPos, stack);
                } else {
                    boolean placed = false;
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        BlockPos adjacent = pos.relative(dir);
                        if (event.getLevel().getBlockState(adjacent).isAir()) {
                            spawnItem(event.getLevel(), adjacent, stack);
                            placed = true;
                            break;
                        }
                    }
                    if (!placed) {
                        spawnItem(event.getLevel(), targetPos, stack);
                    }
                }

                item.discard();
                movedAny = true;
            }

            if (movedAny) {
                ServerLevel serverLevel = (ServerLevel) event.getLevel();
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        30, 0.3, 0.3, 0.3, 0.05);

                player.displayClientMessage(Component.literal("The melody gathers your treasures!").withStyle(ChatFormatting.AQUA), true);
            }
        }
    }

    private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        ItemEntity newItem = new ItemEntity(level, x, y, z, stack.copy());
        newItem.setPickUpDelay(40);
        level.addFreshEntity(newItem);
    }


}
