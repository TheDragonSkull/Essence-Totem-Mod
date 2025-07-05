package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import top.theillusivec4.curios.api.SlotResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ZombieHorseAbility implements IMobAbility {

    private static final Map<UUID, Integer> sprintCounters = new HashMap<>();
    private static final Map<UUID, Boolean> notifiedPlayers = new HashMap<>();
    private static final int REQUIRED_TICKS = 60;

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void zombieHorseJump(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombie_horse"))) return;

        UUID uuid = player.getUUID();

        if (player.isSprinting()) {
            int current = sprintCounters.getOrDefault(uuid, 0) + 1;
            sprintCounters.put(uuid, current);

            if (current >= REQUIRED_TICKS) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, 1, false, false, false));

                if (!notifiedPlayers.getOrDefault(uuid, false)) {
                    player.displayClientMessage(Component.literal("You feel ready to leap!").withStyle(ChatFormatting.GOLD), true);
                    player.level().playSound(null, player.blockPosition(), SoundEvents.HORSE_GALLOP, SoundSource.PLAYERS, 1.0f, 1.0f);
                    notifiedPlayers.put(uuid, true);
                }
            }
        } else {
            sprintCounters.put(uuid, 0);
            notifiedPlayers.put(uuid, false);
        }
    }

    public static void zombieHorseKick(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombie_horse"))) return;

        Entity attackerEntity = event.getSource().getEntity();
        if (!(attackerEntity instanceof LivingEntity attacker)) return;

        Vec3 dirToAttacker = attacker.position().subtract(player.position()).normalize();
        Vec3 backDir = player.getLookAngle().normalize().scale(-1);
        double dot = dirToAttacker.dot(backDir);

        if (dot < 0.5) return;

        if (player.getRandom().nextInt(3) != 0) return;

        Vec3 knockback = backDir.normalize().scale(1.5);
        attacker.setDeltaMovement(attacker.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
        attacker.hurt(attacker.level().damageSources().cactus(), 2);
        attacker.hurtMarked = true;

        player.level().playSound(null, player.blockPosition(), SoundEvents.HORSE_LAND, SoundSource.PLAYERS, 1.0f, 1.0f);

        ((ServerLevel) player.level()).sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + 1, player.getZ(),
                15, 0.5, 0.5, 0.5, 0.1);

        BlockPos under = player.blockPosition().below();
        BlockState blockState = player.level().getBlockState(under);

        for (int i = 0; i < 30; i++) {
            ((ServerLevel) player.level()).sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    player.getX(), player.getY(), player.getZ(),
                    5,
                    0.25, 0.25, 0.25,
                    0.05
            );
        }
    }

    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Optional<SlotResult> zombieTotem = TotemUtils.findTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombie_horse"));

        if (zombieTotem.isEmpty()) return;

        event.setCanceled(true);

        player.setHealth(1.0F);

        ItemStack totemStack = zombieTotem.get().stack();
        CompoundTag tag = totemStack.getTag();
        if (tag != null && tag.contains("Essence")) {
            tag.remove("Essence");
        }

        // VFX
        ServerLevel serverLevel = (ServerLevel) player.level();

        double x = player.getX();
        double y = player.getY() + player.getBbHeight() / 2.0;
        double z = player.getZ();

        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                x, y, z,
                40, 0.5, 0.5, 0.5, 0.02);

        serverLevel.sendParticles(
                ParticleTypes.SOUL,
                x, y, z,
                20,
                0.3, 0.5, 0.3,
                0.01
        );

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.5F, 0.7F);

        player.displayClientMessage(Component.literal("You claw your way back from death...").withStyle(ChatFormatting.DARK_GREEN), true);
    }
}
