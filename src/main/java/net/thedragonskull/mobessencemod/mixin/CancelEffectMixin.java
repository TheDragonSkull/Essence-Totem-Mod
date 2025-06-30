package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class CancelEffectMixin {

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void mobessence$onAddEffect(MobEffectInstance pEffectInstance, Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        MobEffect type = pEffectInstance.getEffect();

        if (self instanceof ServerPlayer player) {

            if (!pEffectInstance.getEffect().isBeneficial() &&
                    pEffectInstance.getEffect() != MobEffects.GLOWING &&
                    pEffectInstance.getEffect() != MobEffects.BAD_OMEN &&
                    TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cow"))) { //todo: los otros 2 totems

                if (player.hasEffect(type)) return;

                if (player.getRandom().nextInt(3) == 0) {

                    player.removeEffect(type);
                    player.connection.send(new ClientboundRemoveMobEffectPacket(player.getId(), type));

                    cir.setReturnValue(false);

                    player.level().playSound(null, player.blockPosition(),
                            SoundEvents.WANDERING_TRADER_DRINK_MILK, SoundSource.PLAYERS, 0.8f, 1.2f);

                    ((ServerLevel) player.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            8, 0.3, 0.5, 0.3, 0.05);

                    player.displayClientMessage(
                            Component.literal("Milk immunity prevented ")
                                    .append(type.getDisplayName())
                                    .append("!"),
                            true
                    );
                }

            }
        }
    }
}

