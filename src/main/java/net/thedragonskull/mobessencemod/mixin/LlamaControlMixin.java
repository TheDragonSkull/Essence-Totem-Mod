package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Llama.class)
public class LlamaControlMixin {

    @Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
    private void allowControl(CallbackInfoReturnable<LivingEntity> cir) {
        Entity self = (Entity)(Object)this;
        if (!(self instanceof Llama llama)) return;

        Entity rider = llama.getFirstPassenger();
        if (rider instanceof ServerPlayer player &&
                (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wandering_trader"))
                        || TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:trader_llama")))) {

            cir.setReturnValue(player);
        }
    }

}
