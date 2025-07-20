package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhantomAbility implements IMobAbility {

    private static final Map<UUID, Boolean> lastJumping = new HashMap<>();

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPhantomTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Phantom)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:phantom"))) {
            event.setNewTarget(null);
        }
    }

    public static void onPlayerPhantomFly(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;
        if (!(event.player instanceof LocalPlayer localPlayer)) return;

        if (!TotemUtils.hasTotemWithEssenceClient(localPlayer, ResourceLocation.parse("minecraft:phantom"))) {
            return;
        }

        if (!isNight(localPlayer.level())) return;

        if (player.onGround() && player.isFallFlying() && player.isInWater() && player.hasEffect(MobEffects.LEVITATION)) return;

        if (localPlayer.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(localPlayer)) return;

        if (localPlayer.isFallFlying()) {
            double yawRad = Math.toRadians(localPlayer.getYRot());
            float offsetScale = 0.2F + 0.21F * 0;
            float sideX = Mth.cos((float) yawRad) * offsetScale;
            float sideZ = Mth.sin((float) yawRad) * offsetScale;
            float heightOffset = 0.3F + 0.25F * 0.0F;

            double baseX = localPlayer.getX();
            double baseY = localPlayer.getY() + heightOffset;
            double baseZ = localPlayer.getZ();

            localPlayer.level().addParticle(ParticleTypes.MYCELIUM, baseX + sideX, baseY, baseZ + sideZ, 0.0D, 0.0D, 0.0D);
            localPlayer.level().addParticle(ParticleTypes.MYCELIUM, baseX - sideX, baseY, baseZ - sideZ, 0.0D, 0.0D, 0.0D);
        }

        if (localPlayer.isFallFlying() && (localPlayer.onGround() || localPlayer.isInWater() || localPlayer.horizontalCollision)) {
            localPlayer.stopFallFlying();
            return;
        }

        boolean prevJumping = lastJumping.getOrDefault(localPlayer.getUUID(), false);
        boolean jumpPressedNow = localPlayer.input.jumping;
        boolean jumpJustPressed = !prevJumping && jumpPressedNow;

        lastJumping.put(localPlayer.getUUID(), jumpPressedNow);

        if (jumpJustPressed
                && !localPlayer.getAbilities().flying
                && !localPlayer.isPassenger()
                && !localPlayer.onClimbable()
                && !localPlayer.isFallFlying()
                && localPlayer.getDeltaMovement().y < -0.1) {

            localPlayer.startFallFlying();

            localPlayer.connection.send(
                    new ServerboundPlayerCommandPacket(localPlayer, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING)
            );
        }

    }

    public static boolean isNight(Level level) {
        if (!level.dimension().equals(Level.OVERWORLD)) return false;
        long time = level.getDayTime() % 24000L;
        return time >= 13000L && time <= 23000L;
    }

    private static final Map<UUID, Boolean> wasFallFlyingLastTick = new HashMap<>();

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        wasFallFlyingLastTick.put(player.getUUID(), player.isFallFlying());
    }

    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        boolean wasFlying = wasFallFlyingLastTick.getOrDefault(player.getUUID(), false);

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:phantom"))) return;

        if (!wasFlying) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FALL)) {
            event.setCanceled(true);
            player.resetFallDistance();
        }
    }


}
