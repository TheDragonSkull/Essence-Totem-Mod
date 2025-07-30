package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class PhantomAbility implements IMobAbility {

    public void tick(ServerPlayer player, ItemStack totemStack) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!chest.is(Items.ELYTRA)) return;
        if (!player.isFallFlying()) return;
        if (player.onGround() || player.isInWater()) return;
        if (player.getCooldowns().isOnCooldown(Items.ELYTRA)) return;

        if (player.isShiftKeyDown()) {
            Vec3 look = player.getLookAngle();
            Vec3 boosted = look.scale(1.5);

            player.setDeltaMovement(boosted);
            player.hurtMarked = true;
            player.startAutoSpinAttack(20);
            //player.getCooldowns().addCooldown(Items.ELYTRA, 20 * 15);

            ((ServerLevel) player.level()).sendParticles(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY(), player.getZ(),
                    30, 0.2, 0.2, 0.2, 0.1
            );

            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.PHANTOM_FLAP,
                    SoundSource.PLAYERS,
                    1.0F, 1.2F
            );
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:phantom"))) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.is(Items.ELYTRA)) return;
        if (!player.isFallFlying()) return;

        double yawRad = Math.toRadians(player.getYRot());
        float offsetScale = 0.2F;
        float sideX = Mth.cos((float) yawRad) * offsetScale;
        float sideZ = Mth.sin((float) yawRad) * offsetScale;
        float heightOffset = 0.3F;

        double baseX = player.getX();
        double baseY = player.getY() + heightOffset;
        double baseZ = player.getZ();

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.MYCELIUM,
                baseX + sideX, baseY, baseZ + sideZ,
                1, 0.0, 0.0, 0.0, 0.0);

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.MYCELIUM,
                baseX - sideX, baseY, baseZ - sideZ,
                1, 0.0, 0.0, 0.0, 0.0);
    }

    public static void onPhantomTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Phantom)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:phantom"))) {
            event.setNewTarget(null);
        }
    }
}
