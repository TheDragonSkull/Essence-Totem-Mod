package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
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
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:silverfish"))) return;

        if (player.getRandom().nextInt(8) != 0) return;

        LivingEntity target = event.getEntity();

        spawnInfestedSilverfish(player, target);
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

        silverfish.setTarget(target);
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

        level.playSound(null, under, blockState.getSoundType().getBreakSound(), SoundSource.HOSTILE, 1.0f, 1.2f);
    }

    public static void onSilverfishTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Silverfish)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:silverfish"))) {
            event.setCanceled(true);
        }
    }
}
