package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public class ChestRemoveMixin {

    @Inject(method = "onRemove", at = @At("HEAD"), cancellable = true)
    private void onRemoveInject(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving, CallbackInfo ci) {
        if (!pState.is(pNewState.getBlock()) && !pLevel.isClientSide()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);

            if (pState.getBlock() != Blocks.CHEST) return;

            if (blockEntity instanceof ChestBlockEntity) {
                ServerPlayer nearbyPlayer = pLevel.getEntitiesOfClass(ServerPlayer.class,
                                new AABB(pPos).inflate(6),
                                player -> TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:donkey")) ||
                                        TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:mule")))
                        .stream().findFirst().orElse(null);

                if (nearbyPlayer != null && !nearbyPlayer.isCreative()) {

                    ci.cancel();
                }
            }
        }
    }

}
