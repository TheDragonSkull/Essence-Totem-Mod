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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class MagmaCubeAbility implements IMobAbility {

    private static final float FALL_DAMAGE_WHEN_REBOUNDING = 1.0f;

    private static final Map<UUID, Double> bounceState = new WeakHashMap<>();
    private static final Map<UUID, Double> fallVelocities = new WeakHashMap<>();

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        UUID id = player.getUUID();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:magma_cube"))) return;

        if (player.onGround() && player.isCrouching()) {
            fallVelocities.remove(id);
        }

        if (!player.onGround() && player.getDeltaMovement().y < 0 && player.fallDistance >= 3) {
            fallVelocities.put(id, player.getDeltaMovement().y);
        }

        if (player.onGround() && fallVelocities.containsKey(id) && !player.isCrouching()) {
            double originalFallSpeed = fallVelocities.remove(id);
            bounceState.put(id, originalFallSpeed);
            double bounceY = -originalFallSpeed * 0.9;

            player.setDeltaMovement(player.getDeltaMovement().x * 0.91, bounceY, player.getDeltaMovement().z * 0.91);
            player.hurtMarked = true;
            player.fallDistance = 0;

            player.level().playSound(null, player.blockPosition(), SoundEvents.MAGMA_CUBE_JUMP, SoundSource.PLAYERS, 1.0f, 1.0f);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.FLAME,
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
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:magma_cube"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypeTags.IS_FALL) && !player.isCrouching()) {
            if (bounceState.containsKey(player.getUUID())) {
                event.setAmount(FALL_DAMAGE_WHEN_REBOUNDING);
                bounceState.remove(player.getUUID());
            }
        }
    }

    private static final Map<UUID, Set<UUID>> recentCollisions = new WeakHashMap<>();

    public static void setOnFireOnContact(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || player.level().isClientSide()) return;
        if (!player.isAlive()) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:magma_cube"))) return;

        UUID playerId = player.getUUID();
        Set<UUID> collidedBefore = recentCollisions.computeIfAbsent(playerId, k -> new HashSet<>());
        Set<UUID> currentlyColliding = new HashSet<>();

        List<LivingEntity> colliding = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(0.1),
                target -> target != player &&
                        target.isAlive() &&
                        !target.isInvulnerable() &&
                        !target.isOnFire() &&
                        target.isPickable()
        );

        for (LivingEntity target : colliding) {
            UUID targetId = target.getUUID();
            currentlyColliding.add(targetId);

            if (!collidedBefore.contains(targetId)) {
                collidedBefore.add(targetId);

                if (player.level().getRandom().nextInt(3) == 0) {
                    double dx = player.getX() - target.getX();
                    double dz = player.getZ() - target.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);

                    if (dist > 0) {
                        double strength = 0.5;
                        dx /= dist;
                        dz /= dist;
                        target.knockback(strength, dx, dz);
                    }

                    player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.MAGMA_CUBE_SQUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                    target.setSecondsOnFire(5);
                }
            }
        }

        collidedBefore.retainAll(currentlyColliding);
    }

}
