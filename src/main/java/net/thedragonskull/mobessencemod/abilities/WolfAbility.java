package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WolfAbility implements IMobAbility {
    private static final Map<UUID, TrackedWolfGroup> activeWolves = new HashMap<>();
    private static final int DURATION_TICKS = 60 * 20;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        UUID uuid = player.getUUID();
        TrackedWolfGroup group = activeWolves.get(uuid);
        if (group == null) return;

        group.ticksLeft--;

        boolean expired = group.ticksLeft <= 0;
        boolean targetDead = group.target.isDeadOrDying();

        if (expired || targetDead) {
            for (Wolf wolf : group.wolves) {
                if (!wolf.isRemoved()) {

                    //todo: sonido al irse + particulas poof

                    wolf.discard();
                }
            }
            activeWolves.remove(uuid);
        }
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wolf"))) return;

        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity target)) return;

        String id = event.getSource().getMsgId();
        if (!(id.equals("player") || id.equals("mob"))) return;

        if (activeWolves.containsKey(player.getUUID())) return;

        if (target.isDeadOrDying()) return;

        if (player.isDeadOrDying()) return;

        ServerLevel level = (ServerLevel) player.level();
        Vec3 backDir = player.getLookAngle().scale(-1).normalize();

        if (player.getRandom().nextInt(6) != 0) return;

        List<Wolf> wolves = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Wolf wolf = EntityType.WOLF.create(level);
            if (wolf == null) continue;

            double spacing = (i == 0 ? 2 : 3);
            Vec3 spawn = player.position().add(backDir.scale(spacing)).add(0, 0.1, 0);

            wolf.moveTo(spawn.x, spawn.y, spawn.z, player.getYRot(), 0.0F);

            wolf.setTarget(target);
            wolf.setLastHurtByMob(target);
            wolf.setOwnerUUID(player.getUUID());
            wolf.setTame(true);
            wolf.setAggressive(true);
            wolf.setPersistenceRequired();

            level.addFreshEntity(wolf);
            wolves.add(wolf);
        }

        activeWolves.put(player.getUUID(), new TrackedWolfGroup(wolves, target));

        level.playSound(null, player.blockPosition(), SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static class TrackedWolfGroup {
        public final List<Wolf> wolves;
        public final LivingEntity target;
        public int ticksLeft;

        public TrackedWolfGroup(List<Wolf> wolves, LivingEntity target) {
            this.wolves = wolves;
            this.target = target;
            this.ticksLeft = DURATION_TICKS;
        }
    }

}

