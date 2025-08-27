package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class PufferfishAbility implements IMobAbility {
    private static final int POISON_DURATION_TICKS = 60;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    private static final Map<UUID, Set<UUID>> recentCollisions = new WeakHashMap<>();

    public static void applyPoisonOnContact(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || player.level().isClientSide()) return;
        if (!player.isAlive()) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:pufferfish"))) return;

        UUID playerId = player.getUUID();
        Set<UUID> collidedBefore = recentCollisions.computeIfAbsent(playerId, k -> new HashSet<>());
        Set<UUID> currentlyColliding = new HashSet<>();

        List<LivingEntity> colliding = player.level().getEntitiesOfClass(
                LivingEntity.class,
                player.getBoundingBox().inflate(0.1),
                target -> target != player &&
                        target.isAlive() &&
                        !target.isInvulnerable() &&
                        target.canBeAffected(new MobEffectInstance(MobEffects.POISON)) &&
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

                    target.hurt(target.level().damageSources().sting(player), 0);

                    player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.PUFFER_FISH_STING, SoundSource.PLAYERS, 1.0F, 1.0F);

                    target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 1));
                }
            }
        }

        collidedBefore.retainAll(currentlyColliding);
    }
}
