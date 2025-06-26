package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TripWireBlock.class)
public class TripWireMixin {

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!(entity instanceof ServerPlayer player)) return;

        if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:parrot"))) {
            ci.cancel();
        }
    }

}
