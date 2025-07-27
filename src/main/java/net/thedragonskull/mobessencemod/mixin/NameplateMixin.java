package net.thedragonskull.mobessencemod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.thedragonskull.mobessencemod.render.NameplateAdjustHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class NameplateMixin {

    @Inject(method = "renderNameTag", at = @At("HEAD"))
    private void adjustNameTag(Entity entity, Component displayName, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (entity instanceof Player player && NameplateAdjustHelper.shouldAdjust(player)) {
            poseStack.translate(0.0D, 0.4D, 0.0D);
        }
    }
}
