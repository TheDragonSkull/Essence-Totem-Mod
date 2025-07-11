package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class RavagerAbility implements IMobAbility {

    private static final float RAM_DAMAGE = 1.5F;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    private static final Map<UUID, Set<UUID>> recentHits = new WeakHashMap<>();

    public static void applyRavagerRamAttack(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!player.isAlive() || !player.isSprinting() || player.isCrouching()) return;
        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:ravager"))) return;

        double speed = player.getSpeed();

        if (speed < 0.12) return;

        AABB hitbox = player.getBoundingBox().inflate(0.1);
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
                LivingEntity.class,
                hitbox,
                target -> target != player && target.isAlive() && target.isPickable() && !target.isInvulnerable()
        );

        UUID playerId = player.getUUID();
        Set<UUID> hitBefore = recentHits.computeIfAbsent(playerId, k -> new HashSet<>());
        Set<UUID> currentlyTouching = new HashSet<>();

        for (LivingEntity target : targets) {
            UUID targetId = target.getUUID();
            currentlyTouching.add(targetId);

            if (!hitBefore.contains(targetId)) {
                hitBefore.add(targetId);

                if (target.hurt(player.level().damageSources().playerAttack(player), RAM_DAMAGE)) {
                    Vec3 dir = player.getLookAngle().normalize();

                    target.knockback(1.5, -dir.x, -dir.z);

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.RAVAGER_ATTACK, SoundSource.PLAYERS, 1.0F, 1.0F);

                }
            }
        }

        hitBefore.retainAll(currentlyTouching);
    }

    public static void onRavagerRoar(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ravager"))) return;

        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof LivingEntity)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.getRandom().nextFloat() >= 0.25F) return;

        List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(3.0),
                e -> e != player && e.isAlive()
        );

        for (LivingEntity target : nearby) {
            float roarDamage = 3.0F;
            target.hurt(player.damageSources().mobAttack(player), roarDamage);

            Vec3 direction = target.position().subtract(player.position()).normalize();
            target.setDeltaMovement(direction.scale(1.5));
        }

        ((ServerLevel)player.level()).sendParticles(
                ParticleTypes.POOF,
                player.getX(), player.getY() + 1, player.getZ(),
                25,
                0.5, 0.5, 0.5,
                0.2
        );

        player.level().playSound(null, player.blockPosition(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void onRavagerKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ravager"))) return;

        float reducedStrength = event.getStrength() * 0.5F;
        event.setStrength(reducedStrength);
    }
}
