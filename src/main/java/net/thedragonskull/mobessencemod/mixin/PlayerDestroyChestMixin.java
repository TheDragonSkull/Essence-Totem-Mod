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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class PlayerDestroyChestMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void onPlayerDestroy(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (level.isClientSide()) return;

        if (state.getBlock() != Blocks.CHEST) return;

        if (blockEntity instanceof ChestBlockEntity chest) {
            if (player instanceof ServerPlayer serverPlayer &&
                    TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:donkey")) &&
                    !serverPlayer.isCreative()) {

                ItemStack stack = new ItemStack(state.getBlock());
                CompoundTag nbt = chest.saveWithFullMetadata();
                BlockItem.setBlockEntityData(stack, chest.getType(), nbt);

                CompoundTag compoundtag = new CompoundTag();
                ListTag listtag = new ListTag();
                listtag.add(StringTag.valueOf("\"(+NBT)\""));
                compoundtag.put("Lore", listtag);
                stack.addTagElement("display", compoundtag);

                ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                level.addFreshEntity(drop);

                ci.cancel();
            }
        }
    }

}
