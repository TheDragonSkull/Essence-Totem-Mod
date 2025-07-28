package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LlamaAbility implements IMobAbility {

    private static final Map<UUID, Long> lastSpitTimes = new HashMap<>();
    private static final int SPIT_COOLDOWN_TICKS = 100;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        long now = player.level().getGameTime();
        UUID uuid = player.getUUID();

        if (now - lastSpitTimes.getOrDefault(uuid, 0L) < SPIT_COOLDOWN_TICKS) return;

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

    public static void onLlamaSpitImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof LlamaSpit spit)) return;
        if (!(event.getRayTraceResult() instanceof EntityHitResult hit)) return;

        if (!(hit.getEntity() instanceof LivingEntity target)) return;
        Entity owner = spit.getOwner();
        if (!(owner instanceof ServerPlayer thrower)) return;

        // Custom dmg
        target.hurt(target.damageSources().mobProjectile(spit, thrower), 2.0F);

        //System.out.println(target.getHealth());

        // Cancel dmg
        event.setImpactResult(ProjectileImpactEvent.ImpactResult.STOP_AT_CURRENT_NO_DAMAGE);

    }

}
