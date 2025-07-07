package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LavaFlowMixin {

    @Inject(method = "travel", at = @At("TAIL"))
    private void applyLavaSwimming(Vec3 travelVector, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LocalPlayer player) {
            if (player.isInLava() && TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:strider"))) {
                float swimSpeed = 0.05F;
                player.moveRelative(swimSpeed, travelVector);

                Vec3 motion = player.getDeltaMovement();
                player.setDeltaMovement(motion.x * 0.9, motion.y, motion.z * 0.9);

                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 0.04, 0));
                } else if (Minecraft.getInstance().options.keyShift.isDown()) {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, -0.04, 0));
                }
            }
        }
    }

}




