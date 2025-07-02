package net.thedragonskull.mobessencemod.abilities;

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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

public class WolfAbility implements IMobAbility {


    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {

    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wolf"))) return;

        DamageSource source = event.getSource();
        Entity cause = source.getEntity();

        if (!(cause instanceof LivingEntity attacker)) return;
        if (attacker.getUUID().equals(player.getUUID())) return;
        if (attacker.isDeadOrDying()) return;

        if (player.isDeadOrDying()) return;

        ServerLevel level = (ServerLevel) player.level();
        Vec3 backDir = player.getLookAngle().scale(-1).normalize();

        if (player.getRandom().nextInt(1) != 0) return; //todo 1/8

        List<Wolf> wolves = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Wolf wolf = EntityType.WOLF.create(level);
            if (wolf == null) continue;

            double spacing = (i == 0 ? 2 : 3);
            Vec3 spawn = player.position().add(backDir.scale(spacing)).add(0, 0.1, 0);
            int despawnTimer = 20 * 60;

            wolf.moveTo(spawn.x, spawn.y, spawn.z, player.getYRot(), 0.0F);

            wolf.setTarget(attacker);
            wolf.setLastHurtByMob(attacker);
            wolf.setOwnerUUID(player.getUUID());
            wolf.setTame(true);
            wolf.setAggressive(true);
            wolf.setPersistenceRequired();

            wolf.getPersistentData().putInt("MobEssence_DespawnTimer", despawnTimer);
            wolf.getPersistentData().putUUID("MobEssence_Owner", player.getUUID());

            level.addFreshEntity(wolf);
            wolves.add(wolf);

            player.displayClientMessage(Component.literal("The pack has your back..."), true);
        }


        level.playSound(null, player.blockPosition(), SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 1.0F, 1.0F);
    }



    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel level : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            for (Wolf wolf : level.getEntities(EntityType.WOLF, Entity::isAlive)) {
                if (wolf.getPersistentData().contains("MobEssence_DespawnTimer")) {
                    int timer = wolf.getPersistentData().getInt("MobEssence_DespawnTimer");

                    if (--timer <= 0 || wolf.getTarget() == null || wolf.getTarget().isDeadOrDying()) {
                        level.sendParticles(ParticleTypes.POOF, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 10, 0.3, 0.3, 0.3, 0.01);
                        level.playSound(null, wolf.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
                        wolf.discard();
                    } else {
                        wolf.getPersistentData().putInt("MobEssence_DespawnTimer", timer);
                    }
                }
            }
        }

    }

}

