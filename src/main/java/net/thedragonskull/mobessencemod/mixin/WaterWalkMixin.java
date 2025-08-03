package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class WaterWalkMixin {

    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                     CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {

        if (!(context instanceof EntityCollisionContext entityCollisionContext)) return;
        if (!(entityCollisionContext.getEntity() instanceof Player player)) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:illusioner"))) return;
        if (player.isCrouching()) return;
        if (player.isInWater()) return;

        FluidState fluidState = state.getFluidState();

        if (fluidState.is(Fluids.WATER) || fluidState.is(Fluids.FLOWING_WATER)) {
            float height = fluidState.getHeight(level, pos);
            float clamped = Math.max(height, 0.01F);

            cir.setReturnValue(Shapes.box(0.0, 0.0, 0.0, 1.0, clamped, 1.0));
        }
    }
}

