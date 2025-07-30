package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PhantomAbility implements IMobAbility {

    public void tick(ServerPlayer player, ItemStack totemStack) {
        Level level = player.level();
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

        if (!chest.is(Items.ELYTRA) || !player.isFallFlying()) return;
        if (player.onGround() || player.isInWater()) return;

        if (player.isShiftKeyDown() && !player.getCooldowns().isOnCooldown(Items.ELYTRA)) {
            int cd = player.level().isNight() ? (20 * 7) : (20 * 15);
            player.getCooldowns().addCooldown(Items.ELYTRA, cd);
            triggerPhantomBoost(player, chest, 1.5F, true, null);
            return;
        }

        AABB inflatedBox = player.getBoundingBox().inflate(1.2);
        AtomicReference<BlockPos> foundBlock = new AtomicReference<>();
        AtomicReference<BlockState> foundState = new AtomicReference<>();

        boolean nearBlock = BlockPos.betweenClosedStream(inflatedBox)
                .anyMatch(pos -> {
                    BlockState state = level.getBlockState(pos);
                    boolean isValid = !state.isAir() && (state.isSolid() || state.is(Blocks.WATER));

                    if (isValid) {
                        foundBlock.set(pos);
                        foundState.set(state);
                    }

                    return isValid;
                });

        if (nearBlock) {
            triggerPhantomBoost(player, chest, 1.0F, false, foundState.get());
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:phantom"))) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.is(Items.ELYTRA) || !player.isFallFlying()) return;

        if (player.horizontalCollision || player.verticalCollision || player.isInWater()) {
            player.stopFallFlying();
            return;
        }

        AABB hitbox = player.getBoundingBox().inflate(0.2);
        List<Entity> collided = player.level().getEntities(player, hitbox, e ->
                e != player && e.isPickable() && e instanceof LivingEntity);

        if (!collided.isEmpty()) {
            player.stopFallFlying();
            return;
        }

        double yawRad = Math.toRadians(player.getYRot());
        float offsetScale = 0.2F;
        float sideX = Mth.cos((float) yawRad) * offsetScale;
        float sideZ = Mth.sin((float) yawRad) * offsetScale;

        double baseX = player.getX();
        double baseY = player.getY();
        double baseZ = player.getZ();

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.MYCELIUM,
                baseX + sideX, baseY, baseZ + sideZ,
                1, 0.0, 0.0, 0.0, 0.0);

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.MYCELIUM,
                baseX - sideX, baseY, baseZ - sideZ,
                1, 0.0, 0.0, 0.0, 0.0);
    }

    private static void triggerPhantomBoost(ServerPlayer player, ItemStack elytra, float boost, boolean cloudParticles, @Nullable BlockState blockParticleSource) {
        Vec3 look = player.getLookAngle();
        Vec3 boosted = look.scale(boost);

        player.setDeltaMovement(boosted);
        player.hurtMarked = true;
        player.startAutoSpinAttack(10);

        ServerLevel level = (ServerLevel) player.level();

        if (cloudParticles) {
            level.sendParticles(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY(), player.getZ(),
                    30, 0.2, 0.2, 0.2, 0.1
            );
        } else if (blockParticleSource != null) {
            if (blockParticleSource.is(Blocks.WATER)) {
                level.sendParticles(
                        ParticleTypes.SPLASH,
                        player.getX(), player.getY(), player.getZ(),
                        30, 0.5, 0.5, 0.5, 0.01
                );
            } else {
                level.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, blockParticleSource),
                        player.getX(), player.getY(), player.getZ(),
                        30, 0.5, 0.5, 0.5, 0.05
                );
            }
        }

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.PHANTOM_FLAP,
                SoundSource.PLAYERS,
                1.0F, 1.0F
        );
    }

    public static void onPhantomTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Phantom)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:phantom"))) {
            event.setNewTarget(null);
        }
    }
}
