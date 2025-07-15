package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SnowGolemAbility implements IMobAbility {

    private static final Map<UUID, Long> lastSnowballTimes = new HashMap<>();
    private static final int SNOWBALL_COOLDOWN_MS = 2000;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        long now = System.currentTimeMillis();
        UUID uuid = player.getUUID();

        if (now - lastSnowballTimes.getOrDefault(uuid, 0L) < SNOWBALL_COOLDOWN_MS) return;

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
        shootSnowball(level, player, target);
        lastSnowballTimes.put(uuid, now);
    }

    private void shootSnowball(Level level, ServerPlayer player, LivingEntity pTarget) {
        Snowball snowball = new Snowball(level, player);

        double d0 = pTarget.getEyeY() - (double)1.1F;
        double d1 = pTarget.getX() - player.getX();
        double d2 = d0 - snowball.getY();
        double d3 = pTarget.getZ() - player.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
        snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
        player.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
        player.level().addFreshEntity(snowball);
    }

    // CommonAbilityUtils.onFreezingHurt
    // PowderSnowBlockMixin
}
