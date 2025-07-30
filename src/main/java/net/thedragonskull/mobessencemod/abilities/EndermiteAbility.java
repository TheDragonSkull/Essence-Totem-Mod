package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class EndermiteAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:endermite"))) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        if (player.getRandom().nextInt(8) != 0) return;

        spawnEndermite(player, attacker);
        spawnEndermite(player, attacker);
    }

    private static void spawnEndermite(ServerPlayer player, LivingEntity target) {
        ServerLevel level = player.serverLevel();

        Endermite endermite = EntityType.ENDERMITE.create(level);
        if (endermite == null) return;

        endermite.moveTo(target.getX(), target.getY(), target.getZ(), level.random.nextFloat() * 360F, 0F);
        ForgeEventFactory.onFinalizeSpawn(
                endermite,
                level,
                level.getCurrentDifficultyAt(target.blockPosition()),
                MobSpawnType.TRIGGERED,
                null,
                null
        );

        endermite.goalSelector.addGoal(1, new MeleeAttackGoal(endermite, 1.0D, true));
        endermite.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(endermite, LivingEntity.class, 10, true, false,
                entity -> entity == target));

        endermite.setTarget(target);
        endermite.setAggressive(true);
        endermite.setPersistenceRequired();
        level.addFreshEntity(endermite);

        player.displayClientMessage(Component.literal("Space folded... and something slipped through..."), true);

        for (int i = 0; i < 3; i++) {
            level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY(), target.getZ(), 30, 0.5, 0.5, 0.5, 1.1);
        }

        level.playSound(null, target.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);
    }

    public static void onEndermiteTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Endermite)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:endermite"))) {
            event.setNewTarget(null);
        }
    }
}
