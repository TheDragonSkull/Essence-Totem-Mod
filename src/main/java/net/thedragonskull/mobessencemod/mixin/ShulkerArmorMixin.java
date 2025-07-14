package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ShulkerArmorMixin {

    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true)
    private void shulkerArmorBoost(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:shulker"))) return;
        if (!player.isCrouching()) return;

        cir.setReturnValue(20);
    }
}
