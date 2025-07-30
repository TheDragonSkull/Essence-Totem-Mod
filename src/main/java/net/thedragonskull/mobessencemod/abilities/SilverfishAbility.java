package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class SilverfishAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:silverfish"))) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        if (player.getRandom().nextInt(5) != 0) return;

        spawnInfestedSilverfish(player, attacker);
        spawnInfestedSilverfish(player, attacker);
    }

    private static void spawnInfestedSilverfish(ServerPlayer player, LivingEntity target) {
        ServerLevel level = player.serverLevel();

        Silverfish silverfish = EntityType.SILVERFISH.create(level);
        if (silverfish == null) return;

        silverfish.moveTo(target.getX(), target.getY(), target.getZ(), level.random.nextFloat() * 360F, 0F);

        ForgeEventFactory.onFinalizeSpawn(
                silverfish,
                level,
                level.getCurrentDifficultyAt(target.blockPosition()),
                MobSpawnType.TRIGGERED,
                null,
                null
        );

        silverfish.goalSelector.addGoal(1, new MeleeAttackGoal(silverfish, 1.0D, true));
        silverfish.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(silverfish, LivingEntity.class, 10, true, false,
                entity -> entity == target));

        silverfish.setTarget(target);
        silverfish.setAggressive(true);
        silverfish.setPersistenceRequired();
        level.addFreshEntity(silverfish);

        player.displayClientMessage(Component.literal("Something crawls out of the cracks..."), true);

        BlockPos under = target.blockPosition().below();
        BlockState blockState = level.getBlockState(under);

        for (int i = 0; i < 30; i++) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    target.getX(), target.getY(), target.getZ(),
                    5,
                    0.25, 0.25, 0.25,
                    0.05
            );
        }

        level.playSound(null, under, blockState.getSoundType().getBreakSound(), SoundSource.HOSTILE, 1.0f, 1.0f);
    }

    public static void onSilverfishTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Silverfish)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:silverfish"))) {
            event.setNewTarget(null);
        }
    }
}
