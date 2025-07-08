package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.resources.ResourceLocation;
import net.thedragonskull.mobessencemod.handlers.MouseHandlerAccessor;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin implements MouseHandlerAccessor {

    @Override
    @Accessor("accumulatedDX")
    public abstract void mobessencemod_setAccumulatedDX(double value);

    @Override
    @Accessor("accumulatedDY")
    public abstract void mobessencemod_setAccumulatedDY(double value);

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void cancelTurnPlayer(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && TotemUtils.hasTotemWithEssenceClient(mc.player, ResourceLocation.parse("minecraft:armor_stand"))) {
            ci.cancel();
        }
    }
}


