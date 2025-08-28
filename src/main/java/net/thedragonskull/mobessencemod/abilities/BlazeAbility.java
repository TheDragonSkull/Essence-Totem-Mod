package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class BlazeAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void blazeLevitate(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:blaze"))) return;

        if (!player.onGround() && player.isCrouching() && !player.isInWater() && !player.isInLava()) {
            Vec3 motion = player.getDeltaMovement();

            player.setDeltaMovement(new Vec3(motion.x, 0.15, motion.z));
            player.hurtMarked = true;

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY(), player.getZ(),
                    2, 0.1, 0.1, 0.1, 0.01);

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SMOKE,
                    player.getX(), player.getY(), player.getZ(),
                    2, 0.1, 0.1, 0.1, 0.01);

            if (player.tickCount % 2 == 0) {
                player.level().playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.5f, 1.0f);
            }
        }

    }

    public static void blazeSetOnFire(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;

        if (!player.getMainHandItem().isEmpty()) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:blaze"))) return;

        LivingEntity target = event.getEntity();

        if (player.getRandom().nextInt(5) == 0 && !target.isOnFire()) {
            target.setSecondsOnFire(5);
        }
    }
}
