package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WebBlock.class)
public class CobWebBlockMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    public void disableSlowness(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (entity instanceof Player player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:fox")) ||
                    TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:spider")) ||
                    TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:cave_spider")) ||
                    TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:snow_fox"))) {
                ci.cancel();
            }
        }
    }

}
