package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class GoatAbility implements IMobAbility {

    private static final float BASE_DAMAGE = 2.0f;
    private static final float MAX_BONUS_DAMAGE = 6.0f;
    private static final double KNOCKBACK_STRENGTH = 0.5;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    private static final Map<UUID, Set<UUID>> recentHits = new WeakHashMap<>();

    public static void applyRamAttack(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!player.isAlive() || !player.isSprinting() || player.isCrouching()) return;
        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:goat"))) return;

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

                if (target.hurt(player.level().damageSources().playerAttack(player), computeDamage(speed))) {
                    Vec3 dir = player.getLookAngle().normalize();

                    target.knockback(computeKnockback(speed), -dir.x, -dir.z);

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.GOAT_RAM_IMPACT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    //player.displayClientMessage(Component.literal("Damage: " + computeDamage(speed)), true);
                }
            }
        }

        hitBefore.retainAll(currentlyTouching);
    }

    private static float computeDamage(double speed) {
        double clamped = Mth.clamp(speed, 0.12, 0.35);
        double percent = (clamped - 0.12) / (0.35 - 0.12);
        return BASE_DAMAGE + (float)(percent * MAX_BONUS_DAMAGE);
    }

    private static double computeKnockback(double speed) {
        double clamped = Mth.clamp(speed, 0.12, 0.25);
        double percent = (clamped - 0.12) / (0.25 - 0.12);
        return 0.5 + percent * 0.75;
    }


}
