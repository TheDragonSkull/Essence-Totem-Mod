package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PhantomElytraMixin {

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void allowPhantomFlight(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;

        if (player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player)) return;

        if (!player.onGround() && !player.isFallFlying() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:phantom"))) {
                cir.setReturnValue(true);
            }
        }
    }

}
