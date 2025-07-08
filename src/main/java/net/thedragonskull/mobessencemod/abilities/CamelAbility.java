package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.network.C2SCamelDashPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class CamelAbility implements IMobAbility {

    private static final String DASH_COOLDOWN_TAG = "mobessencemod_camel_dash_cooldown";
    private static final String DASH_READY_TAG = "mobessencemod_camel_dash_ready";
    private static long lastDashTime = 0;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {

        long currentTime = player.level().getGameTime();
        long cooldownEnd = player.getPersistentData().getLong(DASH_COOLDOWN_TAG);
        boolean dashReady = player.getPersistentData().getBoolean(DASH_READY_TAG);

        if (currentTime >= cooldownEnd && !dashReady) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("You feel ready to dash!")
                            .withStyle(net.minecraft.ChatFormatting.GOLD), true);

            player.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.CAMEL_DASH_READY),
                    SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                    player.level().getRandom().nextLong())
            );

            player.getPersistentData().putBoolean(DASH_READY_TAG, true);
        }

    }

    public static void camelDash(InputEvent.Key event) {

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:camel"))) return;

        if (mc.options.keySprint.consumeClick()) {
            if (player.isSprinting() && !player.isInWater() && player.onGround() && !player.isInLava() && !player.isSwimming()) {
                long now = System.currentTimeMillis();
                if (now - lastDashTime >= 5000) {
                    PacketHandler.sendToServer(new C2SCamelDashPacket());
                    lastDashTime = now;
                }
            }
        }
    }

    public static void tryDash(Player player) {

        long currentTime = player.level().getGameTime();
        long cooldownEnd = player.getPersistentData().getLong(DASH_COOLDOWN_TAG);

        if (currentTime < cooldownEnd) return;

        Vec3 dash = player.getLookAngle().scale(2.0);
        player.setDeltaMovement(dash.x, player.getDeltaMovement().y + 0.2, dash.z);
        player.hurtMarked = true;

        player.getPersistentData().putLong(DASH_COOLDOWN_TAG, currentTime + 100);
        player.getPersistentData().putBoolean(DASH_READY_TAG, false);

        player.level().playSound(null, player.blockPosition(), SoundEvents.CAMEL_DASH, SoundSource.PLAYERS, 1.0f, 1.0f);

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 1, player.getZ(),
                15, 0.5, 0.5, 0.5, 0.1);
    }

    public static void onCactusHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:camel"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.CACTUS)) {
            event.setCanceled(true);
        }
    }

    public static void camelStep(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        AttributeInstance stepHeight = serverPlayer.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeight == null) return;

        if (hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:camel"))) {
            stepHeight.setBaseValue(0.9f);
        } else if (stepHeight.getBaseValue() != 0) {
            stepHeight.setBaseValue(0);
        }
    }
}
