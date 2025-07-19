package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Zoglin.class)
public class ZoglinMixin {

    @Inject(method = "findNearestValidAttackTarget", at = @At("HEAD"), cancellable = true)
    private void mobessence$preventTargetPlayerWithTotem(CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        Zoglin self = (Zoglin) (Object) this;
        Optional<NearestVisibleLivingEntities> visible = self.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        if (visible.isEmpty()) return;

        for (LivingEntity entity : visible.get().findAll(target -> {
            EntityType<?> type = target.getType();
            return type != EntityType.ZOGLIN &&
                    type != EntityType.CREEPER &&
                    Sensor.isEntityAttackable(self, target);
        })) {
            if (entity instanceof Player player) {
                if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:zoglin"))) {
                    cir.setReturnValue(Optional.empty());
                    return;
                }
            }

            cir.setReturnValue(Optional.of(entity));
            return;
        }

        cir.setReturnValue(Optional.empty());
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void mobessence$clearAttackTargetIfTotemHeld(CallbackInfo ci) {
        Zoglin zoglin = (Zoglin) (Object) this;
        Brain<?> brain = zoglin.getBrain();

        Optional<LivingEntity> attackTarget = brain.getMemory(MemoryModuleType.ATTACK_TARGET);

        if (attackTarget.isPresent() && attackTarget.get() instanceof Player player) {
            if (TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:zoglin"))) {
                brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
                brain.eraseMemory(MemoryModuleType.WALK_TARGET);
                brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
                brain.eraseMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
                brain.eraseMemory(MemoryModuleType.ANGRY_AT);
            }
        }
    }
}
