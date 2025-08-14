package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WardenAbility implements IMobAbility {

    private static final Map<UUID, Integer> angerLevels = new HashMap<>();
    private static final Map<UUID, Long> lastAngerTick = new HashMap<>();

    private static final Map<UUID, ServerBossEvent> bossBars = new HashMap<>();

    private static final int MAX_ANGER = 10;

    public static int getAnger(ServerPlayer player) {
        return angerLevels.getOrDefault(player.getUUID(), 0);
    }

    public static boolean isAngry(ServerPlayer player) {
        return getAnger(player) > 0;
    }

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        UUID id = player.getUUID();
        int anger = getAnger(player);
        long gameTime = player.level().getGameTime();

        // Bossbar
        if (anger > 0) {
            bossBars.computeIfAbsent(id, uuid -> {
                ServerBossEvent bar = new ServerBossEvent(
                        Component.literal("ANGER"),
                        BossEvent.BossBarColor.WHITE,
                        BossEvent.BossBarOverlay.PROGRESS
                );
                bar.addPlayer(player);
                return bar;
            });

            ServerBossEvent bar = bossBars.get(id);

            bar.setProgress(anger / (float) MAX_ANGER);
            bar.setColor(getColorForAnger(anger));
            bar.setVisible(true);
        } else {
            if (bossBars.containsKey(id)) {
                ServerBossEvent bar = bossBars.remove(id);
                bar.removePlayer(player);
            }
        }

        // Increment anger
        long last = lastAngerTick.getOrDefault(id, gameTime);
        if (anger > 0 && gameTime - last >= 200) {
            int newAnger = Math.min(anger + 1, MAX_ANGER);
            setAnger(player, newAnger);
            lastAngerTick.put(id, gameTime);
        }

        // Glowing skill
        if (player.isShiftKeyDown()) {
            List<LivingEntity> nearbyEnemies = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(15),
                    entity -> (entity instanceof Mob || entity instanceof Player) && entity != player);

            for (LivingEntity enemy : nearbyEnemies) {
                enemy.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2, 0, false, false));
            }

            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 22, 255, false, false));
        }

    }

    private static BossEvent.BossBarColor getColorForAnger(int anger) {
        if (anger <= 2) return BossEvent.BossBarColor.GREEN;
        if (anger <= 4) return BossEvent.BossBarColor.YELLOW;
        if (anger <= 6) return BossEvent.BossBarColor.RED;
        if (anger <= 8) return BossEvent.BossBarColor.PINK;
        return BossEvent.BossBarColor.PURPLE;
    }

    public static void onPlayerHurt(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        Entity target = event.getEntity();

        // Player DMG reduction
        if (target instanceof ServerPlayer player) {
            if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warden"))) return;

            UUID id = player.getUUID();
            int anger = Math.min(getAnger(player) + 1, MAX_ANGER);

            setAnger(player, anger);
            lastAngerTick.put(id, player.level().getGameTime());

            float reduced = event.getAmount() / (1 + 0.2f * anger);
            event.setAmount(reduced);
        }

        // Target DMG increase
        if (source instanceof ServerPlayer player) {
            if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warden"))) return;

            int anger = getAnger(player);
            if (anger > 0) {
                float increased = event.getAmount() * (1 + 0.2f * anger);
                event.setAmount(increased);
            }
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID id = player.getUUID();

        boolean hasTotem = TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warden"));
        boolean isAngry = angerLevels.containsKey(id);

        if (!hasTotem && isAngry) {
            resetAnger(player);
        }
    }

    public static void onKillEntity(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:warden"))) return;

        if (isAngry(player)) {
            player.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.WARDEN_ATTACK_IMPACT),
                    SoundSource.PLAYERS,
                    player.getX(), player.getY(), player.getZ(),
                    1.0f,
                    0.4f / (player.getRandom().nextFloat() * 0.4f + 0.8f),
                    player.level().getRandom().nextLong()
            ));
        }

        resetAnger(player);
    }

    public static void setAnger(ServerPlayer player, int anger) {
        UUID id = player.getUUID();

        if (anger <= 0) {
            resetAnger(player);
            return;
        }

        angerLevels.put(id, anger);
        player.getPersistentData().putInt("wardenAnger", anger);

        ServerBossEvent bar = bossBars.computeIfAbsent(id, uuid ->
                new ServerBossEvent(
                        Component.literal("ANGER"),
                        getColorForAnger(anger),
                        BossEvent.BossBarOverlay.PROGRESS
                )
        );

        bar.addPlayer(player);

        bar.setProgress(anger / (float) MAX_ANGER);
        bar.setColor(getColorForAnger(anger));
        bar.setVisible(true);
    }

    public static void resetAnger(ServerPlayer player) {
        UUID id = player.getUUID();
        angerLevels.remove(id);
        lastAngerTick.remove(id);
        player.getPersistentData().remove("wardenAnger");

        ServerBossEvent bar = bossBars.get(id);
        if (bar != null) {
            bar.removePlayer(player);
        }
    }

    public static void restoreAnger(ServerPlayer player, int anger) {
        setAnger(player, Math.min(Math.max(anger, 0), MAX_ANGER));
    }


    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        int anger = player.getPersistentData().getInt("wardenAnger");
        if (anger > 0) {
            restoreAnger(player, anger);
        }
    }
}
