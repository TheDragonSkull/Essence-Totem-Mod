package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class SlimeAbility implements IMobAbility {
    private static final float FALL_DAMAGE_WHEN_REBOUNDING = 1.0f;

    private static final Map<UUID, Double> bounceState = new WeakHashMap<>();
    private static final Map<UUID, Double> fallVelocities = new WeakHashMap<>();

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        UUID id = player.getUUID();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:slime"))) return;

        if (player.onGround() && player.isShiftKeyDown()) {
            fallVelocities.remove(id);
        }

        if (!player.onGround() && player.getDeltaMovement().y < 0 && player.fallDistance >= 3) {
            fallVelocities.put(id, player.getDeltaMovement().y);
        }

        if (player.onGround() && fallVelocities.containsKey(id) && !player.isShiftKeyDown()) {
            double originalFallSpeed = fallVelocities.remove(id);
            bounceState.put(id, originalFallSpeed);
            double bounceY = -originalFallSpeed * 0.9;

            player.setDeltaMovement(player.getDeltaMovement().x * 0.91, bounceY, player.getDeltaMovement().z * 0.91);
            player.hurtMarked = true;
            player.fallDistance = 0;

            player.level().playSound(null, player.blockPosition(), SoundEvents.SLIME_JUMP, SoundSource.PLAYERS, 1.0f, 1.0f);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.ITEM_SLIME,
                        player.getX(), player.getY() + 0.1, player.getZ(),
                        8,
                        0.3, 0.1, 0.3,
                        0.05
                );
            }
        }
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:slime"))) return;

        DamageSource source = event.getSource();

        // Part 1: fall damage reduction
        if (source.is(DamageTypeTags.IS_FALL) && !player.isShiftKeyDown()) {
            if (bounceState.containsKey(player.getUUID())) {
                event.setAmount(FALL_DAMAGE_WHEN_REBOUNDING);
                bounceState.remove(player.getUUID());
            }
            return;
        }

        // Part 2: Absorb damage
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.level().getRandom().nextInt(4) == 0) {
            event.setAmount(0.0F);
            event.setCanceled(true);

            Vec3 knockback = player.position().subtract(attacker.position()).normalize().scale(2);
            player.setDeltaMovement(knockback.x, player.getDeltaMovement().y, knockback.z);
            player.hurtMarked = true;

            player.level().playSound(null, player.blockPosition(), SoundEvents.SLIME_ATTACK, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
