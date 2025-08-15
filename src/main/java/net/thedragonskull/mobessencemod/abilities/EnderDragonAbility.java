package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.thedragonskull.mobessencemod.block.ModBlocks;
import net.thedragonskull.mobessencemod.network.C2SFlapActionPacket;
import net.thedragonskull.mobessencemod.network.C2SFlapSoundPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotResult;

import java.util.*;

public class EnderDragonAbility implements IMobAbility {

    private static final Map<UUID, Integer> flapCount = new HashMap<>();
    private static final Map<UUID, Boolean> wasJumpKeyDown = new HashMap<>();

    private static final int MAX_FLAPS = 5;
    private static final double MIN_FALL_DISTANCE = 5.0;

    private static final Map<UUID, Long> dragonBreathCooldowns = new HashMap<>();
    private static final Set<UUID> notifiedReady = new HashSet<>();
    private static final long DRAGON_BREATH_COOLDOWN_TICKS = 20 * 30;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        if (player.onGround() || player.isInWater() || player.isInLava() || player.isSwimming()) {
            flapCount.put(player.getUUID(), 0);
        }
    }

    public static void onDragonHeal(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;

        ServerPlayer player = (ServerPlayer) event.player;
        Level level = player.level();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon"))) return;

        BlockPos playerPos = player.blockPosition();
        int radius = 5;
        boolean foundAny = false;

        for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-radius, -radius, -radius), playerPos.offset(radius, radius, radius))) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(Blocks.END_ROD)) {
                BlockState newState = ModBlocks.HEALING_END_ROD.get()
                        .defaultBlockState()
                        .setValue(EndRodBlock.FACING, blockState.getValue(EndRodBlock.FACING));

                level.setBlock(pos, newState, 3);
                foundAny = true;
            }
        }

        if (!foundAny) return;

        if (player.tickCount % 20 == 0) {
            player.heal(0.5f);
        }
    }

    public static void onDragonBreath(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon"))) return;

        UUID uuid = player.getUUID();
        long currentTick = player.level().getGameTime();
        long lastUse = dragonBreathCooldowns.getOrDefault(uuid, 0L);
        long timeSince = currentTick - lastUse;

        if (timeSince >= DRAGON_BREATH_COOLDOWN_TICKS && !notifiedReady.contains(uuid)) {
            player.connection.send(new ClientboundSoundPacket(
                    BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.ENDER_DRAGON_GROWL),
                    SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0f, 1.0f,
                    player.level().getRandom().nextLong())
            );

            player.displayClientMessage(
                    Component.literal("You feel ready to do the Dragon Breath!").withStyle(ChatFormatting.GOLD),
                    true
            );
            notifiedReady.add(uuid);
        }

        if (player.onGround() && flapCount.getOrDefault(uuid, 0) >= MAX_FLAPS) {
            if (player.fallDistance >= MIN_FALL_DISTANCE && timeSince >= DRAGON_BREATH_COOLDOWN_TICKS) {
                player.fallDistance = 0.0F;
                spawnDragonBreath(player);
                dragonBreathCooldowns.put(uuid, currentTick);
                notifiedReady.remove(uuid);
                flapCount.put(uuid, 0);
            }

            flapCount.put(uuid, 0);
        }
    }

    private static void spawnDragonBreath(ServerPlayer player) {
        AreaEffectCloud cloud = new AreaEffectCloud(player.level(), player.getX(), player.getY(), player.getZ());
        cloud.setRadius(3.5F);
        cloud.setDuration(100);
        cloud.setParticle(ParticleTypes.DRAGON_BREATH);
        cloud.setOwner(player);
        cloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 0));
        player.level().addFreshEntity(cloud);

        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 2.0f, 1.0f);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_AMBIENT, SoundSource.PLAYERS, 2.0f, 1.0f);

        BlockPos under = player.blockPosition().below();
        BlockState blockState = player.level().getBlockState(under);

        ((ServerLevel) player.level()).sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                player.getX(), player.getY(), player.getZ(),
                100,
                0.5, 0.5, 0.5,
                0.1
        );
    }

    public static void onEffectAdded(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.DRAGON_BREATH)) {
            event.setCanceled(true);
        }

        if (source.getDirectEntity() instanceof AreaEffectCloud cloud) {
            if (cloud.getParticle() == ParticleTypes.DRAGON_BREATH || hasHarmEffect(cloud)) {
                event.setCanceled(true);
            }
        }
    }

    private static boolean hasHarmEffect(AreaEffectCloud cloud) {
        return cloud.getPotion().getEffects().stream().anyMatch(e -> e.getEffect() == MobEffects.HARM);
    }

    public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon"))) return;

        if (flapCount.getOrDefault(uuid, 0) >= MAX_FLAPS) {
            long currentTick = player.level().getGameTime();
            long lastUse = EnderDragonAbility.dragonBreathCooldowns.getOrDefault(uuid, 0L);

            if ((currentTick - lastUse) >= EnderDragonAbility.DRAGON_BREATH_COOLDOWN_TICKS) {
                event.setCanceled(true);
            }
        }
    }

    public static void handleFlapPacket(ServerPlayer player) {
        UUID uuid = player.getUUID();
        int currentFlaps = flapCount.getOrDefault(uuid, 0);

        boolean isFlying = player.getAbilities().flying;
        boolean isEligible = !player.onGround() && !isFlying && !player.isInWater() && !player.isInLava() && !player.isSwimming();

        if (isEligible && currentFlaps < MAX_FLAPS) {
            Vec3 motion = player.getDeltaMovement();
            double verticalBoost = 0.45 + (0.02 * currentFlaps);
            player.setDeltaMovement(motion.x, verticalBoost, motion.z);
            player.hasImpulse = true;
            player.hurtMarked = true;

            player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0f, 1.0f);

            flapCount.put(uuid, currentFlaps + 1);
        }
    }

    public static void flapClient(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:ender_dragon")))
            return;

        UUID uuid = player.getUUID();
        boolean jumpKeyDown = mc.options.keyJump.isDown();
        boolean prevState = wasJumpKeyDown.getOrDefault(uuid, false);

        if (jumpKeyDown && !prevState) {
            PacketHandler.sendToServer(new C2SFlapActionPacket());
        }

        wasJumpKeyDown.put(uuid, jumpKeyDown);
    }
}
