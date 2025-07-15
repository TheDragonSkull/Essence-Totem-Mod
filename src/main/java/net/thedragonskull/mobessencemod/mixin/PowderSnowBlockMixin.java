package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {

    @Inject(method = "canEntityWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void allowSnowFoxTotem(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:snow_fox")) ||
                    TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:snow_golem"))) {
                cir.setReturnValue(true);
            }
        }
    }

}
