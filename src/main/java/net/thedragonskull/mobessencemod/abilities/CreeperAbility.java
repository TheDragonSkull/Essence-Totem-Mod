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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class CreeperAbility implements IMobAbility {

    private static final Map<UUID, Long> lastExplosionTick = new WeakHashMap<>();
    private static final long COOLDOWN_TICKS = 40;
    private static final float EXPLOSION_RADIUS = 1.5f;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:creeper"))) return;

        DamageSource source = event.getSource();

        if (!(source.getEntity() instanceof LivingEntity)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        long gameTime = player.level().getGameTime();
        UUID playerId = player.getUUID();

        long lastTime = lastExplosionTick.getOrDefault(playerId, -COOLDOWN_TICKS);
        if (gameTime - lastTime < COOLDOWN_TICKS) return;

        // 1/3
        if (player.level().getRandom().nextInt(3) != 0) return;

        lastExplosionTick.put(playerId, gameTime);

        ServerLevel level = player.serverLevel();

        level.explode(
                player,
                player.getX(), player.getY(), player.getZ(),
                1.5F,
                false,
                Level.ExplosionInteraction.NONE
        );

        level.sendParticles(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 0.5, player.getZ(), 5, 0.45, 0.3, 0.45, 0.02);
        level.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 0.5, player.getZ(), 8, 0.4, 0.2, 0.4, 0.02);

        level.playSound(null, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

}
