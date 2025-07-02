package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TraderLlamaAbility implements IMobAbility {

    private static final Map<UUID, Long> lastSpitTimes = new HashMap<>();
    private static final int SPIT_COOLDOWN_MS = 5000;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        long now = System.currentTimeMillis();
        UUID uuid = player.getUUID();

        if (now - lastSpitTimes.getOrDefault(uuid, 0L) < SPIT_COOLDOWN_MS) return;

        double range = 10.0;
        Level level = player.level();

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(range),
                e -> e instanceof Mob mob &&
                        !(mob instanceof Llama) &&
                        mob.getTarget() == player &&
                        mob.isAlive()
        );

        if (targets.isEmpty()) return;

        LivingEntity target = targets.get(0);
        shootSpit(level, player, target);
        lastSpitTimes.put(uuid, now);
    }

    private void shootSpit(Level level, ServerPlayer player, LivingEntity target) {
        LlamaSpit spit = new LlamaSpit(EntityType.LLAMA_SPIT, level);

        spit.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());

        double dx = target.getX() - player.getX();
        double dy = target.getY(0.333) - spit.getY();
        double dz = target.getZ() - player.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz) * 0.2;

        spit.shoot(dx, dy + dist, dz, 1.5F, 10.0F);
        spit.setOwner(player);

        level.addFreshEntity(spit);
        level.playSound(null, player.blockPosition(), SoundEvents.LLAMA_SPIT, SoundSource.PLAYERS, 1.0F, 1.0F);

    }

    public static void onTraderLlamaSpitImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof LlamaSpit spit)) return;
        if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;

        if (!(hit.getEntity() instanceof LivingEntity target)) return;
        Entity owner = spit.getOwner();
        if (!(owner instanceof ServerPlayer thrower)) return;

        // Custom dmg
        target.hurt(target.damageSources().mobProjectile(spit, thrower), 2.0F);

        System.out.println(target.getHealth());

        // Cancel dmg
        event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT_NO_DAMAGE);

    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:trader_llama"))) return;

        applyCaravanAura(player);
    }

    private static final double AURA_RADIUS = 5.0;

    private static void applyCaravanAura(ServerPlayer player) {
        Level level = player.level();
        AABB area = player.getBoundingBox().inflate(AURA_RADIUS);

        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, area, mob -> {
            if (mob.isDeadOrDying()) return false;

            MobCategory mobCategory = mob.getType().getCategory();
            if (mobCategory == MobCategory.MISC ||
                    mobCategory == MobCategory.MONSTER) return false;

            if (mob.getTarget() != null || mob.isPassenger() || mob.isVehicle()) return false;

            return true;
        });

        for (Mob mob : nearbyMobs) {
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
