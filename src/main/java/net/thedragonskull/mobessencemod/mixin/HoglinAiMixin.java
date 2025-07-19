package net.thedragonskull.mobessencemod.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.hoglin.HoglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(HoglinAi.class)
public class HoglinAiMixin {

    @Inject(method = "updateActivity", at = @At("HEAD"), cancellable = true)
    private static void mobessence$preventFightActivity(Hoglin hoglin, CallbackInfo ci) {
        Optional<LivingEntity> target = hoglin.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (target.isPresent() && target.get() instanceof Player player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:hoglin"))) {
                ci.cancel();

                hoglin.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.IDLE));
                hoglin.setAggressive(false);
            }
        }
    }
}
