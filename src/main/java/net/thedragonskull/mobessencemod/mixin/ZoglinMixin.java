package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zoglin.class)
public class ZoglinMixin {

    @Inject(
            method = "isTargetable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mobessence_isTargetable(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (target instanceof Player player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:zoglin"))) {
                cir.setReturnValue(false);
            }
        }
    }
}
