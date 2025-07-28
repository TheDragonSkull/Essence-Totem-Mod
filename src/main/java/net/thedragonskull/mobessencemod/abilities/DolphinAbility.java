package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DolphinAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void swimBoost(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (player == null || !player.level().isClientSide()) return;

        if (!player.isUnderWater() || !player.isSprinting()) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:dolphin")))
            return;

        Minecraft mc = Minecraft.getInstance();

        boolean moving =
                mc.options.keyUp.isDown() ||
                        mc.options.keyDown.isDown() ||
                        mc.options.keyLeft.isDown() ||
                        mc.options.keyRight.isDown();

        if (!moving) return;

        Vec3 current = player.getDeltaMovement();
        Vec3 forward = player.getLookAngle().normalize().scale(0.025);

        Vec3 boosted = current.add(forward.x, forward.y, forward.z);
        player.setDeltaMovement(boosted);
    }

    public static void dolphinAutoSpinDash(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:dolphin"))) return;

        if (!player.isUnderWater() || !player.isSwimming()) return;

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.BUBBLE,
                player.getX(), player.getY() + 0.5, player.getZ(),
                6, 0.3, 0.3, 0.3, 0.02);


        boolean nearbyEntity = !player.level().getEntities(player, player.getBoundingBox().inflate(1.5),
                e -> e != player && e.isAlive() && e instanceof net.minecraft.world.entity.LivingEntity).isEmpty();

        if (nearbyEntity) {
            Vec3 dash = player.getLookAngle().normalize().scale(1.2);
            player.setDeltaMovement(player.getDeltaMovement().add(dash.x, 0.1, dash.z));
            player.hurtMarked = true;

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SONIC_BOOM,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    1, 0.5, 0.5, 0.5, 0.02);

            player.level().playSound(null, player.blockPosition(), SoundEvents.DOLPHIN_SWIM, SoundSource.PLAYERS, 1.0f, 1.0f);
            player.startAutoSpinAttack(20);
        }
    }


}
