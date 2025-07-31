package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class RabbitJumpMixin {

    @Inject(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", shift = At.Shift.AFTER))
    private void mobessence$increaseJumpPower(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!(entity instanceof Player player)) return;
        if (!(TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:rabbit")) ||
                TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:killer_bunny")))) return;

        Vec3 current = player.getDeltaMovement();

        double boost = 0.15D;

        player.setDeltaMovement(current.x, current.y + boost, current.z);
    }
}
