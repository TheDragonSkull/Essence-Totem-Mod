package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlowSquidAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:glow_squid"))) return;

        DamageSource source = event.getSource();

        if (player.getRandom().nextInt(3) != 0) return;

        if (!(source.getEntity() instanceof LivingEntity)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.GLOW_SQUID_INK,
                player.getX(), player.getY() + 1, player.getZ(),
                30, 0.5, 0.5, 0.5, 0.1);

        player.level().playSound(null, player.blockPosition(), SoundEvents.GLOW_SQUID_SQUIRT, SoundSource.PLAYERS, 1.0f, 1.0f);

        Entity attacker = event.getSource().getEntity();
        if (attacker instanceof LivingEntity livingAttacker) {
            livingAttacker.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        }

        if (attacker != null && player.isUnderWater()) {
            Vec3 away = player.position().subtract(attacker.position()).normalize().scale(1.5);
            player.setDeltaMovement(player.getDeltaMovement().add(away.x, 0.3, away.z));
            player.hurtMarked = true;
        }
    }

    private static final Set<Integer> processedMobs = new HashSet<>();

    public static void followGlowSquid(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (event.side.isServer() && event.player.tickCount % 2 == 0) {
            processedMobs.clear();
        }

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:glow_squid"))) return;

        applyGlowingSquidAura(player);
    }

    private static final double AURA_RADIUS = 10.0;

    private static void applyGlowingSquidAura(ServerPlayer player) {
        Level level = player.level();
        AABB area = player.getBoundingBox().inflate(AURA_RADIUS);

        if (!player.isUnderWater()) return;

        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, area, mob -> {
            if (processedMobs.contains(mob.getId())) return false;
            if (mob.isDeadOrDying()) return false;

            MobCategory mobCategory = mob.getType().getCategory();
            if (mobCategory == MobCategory.MISC ||
                    mobCategory == MobCategory.MONSTER ||
                    mobCategory == MobCategory.AMBIENT ||
                    mobCategory == MobCategory.CREATURE
            ) return false;

            if (mob instanceof Squid) return false;

            if (mob.getTarget() != null || mob.isPassenger() || mob.isVehicle()) return false;

            if (mob.isLeashed() && !(mob.getLeashHolder() instanceof Player)) return false;

            return true;
        });

        for (Mob mob : nearbyMobs) {
            processedMobs.add(mob.getId());

            double distance = mob.distanceTo(player);

            if (distance > AURA_RADIUS) {
                if (isFollowingPlayer(mob, player)) {
                    mob.getNavigation().stop();
                    mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                }
                continue;
            }

            if (!isFollowingPlayer(mob, player)) {
                Vec3 pos = player.getEyePosition();
                mob.getNavigation().moveTo(pos.x, pos.y, pos.z, 1.0);

                ((ServerLevel) mob.level()).sendParticles(ParticleTypes.GLOW,
                        mob.getX(), mob.getY(), mob.getZ(),
                        5, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    private static boolean isFollowingPlayer(Mob mob, Player player) {
        Path path = mob.getNavigation().getPath();
        if (path == null || path.isDone()) return false;

        BlockPos targetPos = path.getTarget();

        Vec3 targetVec = Vec3.atCenterOf(targetPos);
        return targetVec.distanceTo(player.position()) < 2.5;
    }
}
