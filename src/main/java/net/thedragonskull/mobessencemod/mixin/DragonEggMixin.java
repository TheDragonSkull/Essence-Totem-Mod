package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DragonEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.thedragonskull.mobessencemod.item.custom.TotemOfEssenceItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonEggBlock.class)
public class DragonEggMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void mobessence$onUse(BlockState state, Level level, BlockPos pos,
                                  Player player, InteractionHand hand, BlockHitResult hit,
                                  CallbackInfoReturnable<InteractionResult> cir) {

        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof TotemOfEssenceItem && !player.isShiftKeyDown()) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }

}
