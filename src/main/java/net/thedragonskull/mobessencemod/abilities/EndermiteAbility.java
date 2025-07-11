package net.thedragonskull.mobessencemod.abilities;

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
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:endermite"))) return;

        if (player.getRandom().nextInt(12) != 0) return;

        LivingEntity target = event.getEntity();

        spawnEndermite(player, target);
        spawnEndermite(player, target);
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

        endermite.setTarget(target);
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
