package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
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
import net.thedragonskull.mobessencemod.network.C2SFlapSoundPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnderDragonAbility implements IMobAbility {

    private static final Map<UUID, Integer> flapCount = new HashMap<>();
    private static boolean wasJumpKeyDown = false;

    private static final int MAX_FLAPS = 5;

    private static final Map<UUID, Double> fallStartY = new HashMap<>();
    private static final double MIN_FALL_DISTANCE = 5.0;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        if (player.onGround() || player.isInWater() || player.isInLava() || player.isSwimming()) {
            flapCount.remove(player.getUUID());
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

        if (player.onGround() && flapCount.getOrDefault(uuid, 0) >= MAX_FLAPS) {
            if (player.fallDistance >= MIN_FALL_DISTANCE) {
                player.fallDistance = 0.0F;

                AreaEffectCloud cloud = new AreaEffectCloud(player.level(), player.getX(), player.getY(), player.getZ());
                cloud.setRadius(3.5F);
                cloud.setDuration(100);
                cloud.setParticle(ParticleTypes.DRAGON_BREATH);
                cloud.setOwner(player);
                cloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 0));
                player.level().addFreshEntity(cloud);

                player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.PLAYERS, 2.0f, 1.0f);
                player.level().playSound(null, player.blockPosition(), SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 2.0f, 1.0f);

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

            flapCount.put(uuid, 0);
        }
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
        if (event.getEntity() instanceof ServerPlayer player &&
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ender_dragon")) &&
                flapCount.getOrDefault(player.getUUID(), 0) >= MAX_FLAPS) {

            event.setCanceled(true);
        }
    }

    public static void flap(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:ender_dragon")))
            return;

        UUID uuid = player.getUUID();
        int currentFlaps = flapCount.getOrDefault(uuid, 0);
        boolean jumpKeyDown = mc.options.keyJump.isDown();

        if (jumpKeyDown && !wasJumpKeyDown) {
            boolean isFlying = player.getAbilities().flying;
            boolean isEligible = !player.onGround() && !isFlying && !player.isInWater() && !player.isInLava() && !player.isSwimming();

            if (isEligible && currentFlaps < MAX_FLAPS) {
                Vec3 motion = player.getDeltaMovement();
                double verticalBoost = 0.45 + (0.02 * currentFlaps);
                player.setDeltaMovement(motion.x, verticalBoost, motion.z);
                player.hasImpulse = true;

                PacketHandler.sendToServer(new C2SFlapSoundPacket(ResourceLocation.parse("minecraft:ender_dragon")));

                flapCount.put(uuid, currentFlaps + 1);
            }
        }

        wasJumpKeyDown = jumpKeyDown;
    }
}
