package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Player.class)
public class NoUnderwaterMiningPenaltyMixin {

    @Inject(method = "getDigSpeed", at = @At("TAIL"), cancellable = true, remap = false)
    private void bypassUnderwaterMiningPenalty(BlockState state, @Nullable BlockPos pos, CallbackInfoReturnable<Float> cir) {
        Player player = (Player) (Object) this;

        if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:elder_guardian"))) {
            float value = cir.getReturnValueF();

            if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !EnchantmentHelper.hasAquaAffinity(player)) {
                value *= 5.0F;
            }

            if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !player.onGround()) {
                value *= 5.0F;
            }

            cir.setReturnValue(value);
        }
    }
}
