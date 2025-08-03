package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.entity.ModEntities;
import net.thedragonskull.mobessencemod.entity.custom.IllusionDecoyEntity;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.joml.Vector3f;

import static net.thedragonskull.mobessencemod.entity.custom.IllusionDecoyEntity.createPlayerHead;

public class IllusionerAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onWaterWalk(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        Level level = player.level();

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:illusioner"))) return;
        if (!player.isSprinting()) return;
        if (player.isCrouching()) return;
        if (player.isInWater()) return;

        BlockPos below = BlockPos.containing(player.getX(), player.getY() - 0.1, player.getZ());
        BlockState belowState = level.getBlockState(below);
        if (!belowState.getFluidState().is(Fluids.WATER)) return;

        double speed = player.getDeltaMovement().horizontalDistanceSqr();
        double splashPower = Mth.clamp(speed * 20.0, 0.1, 2.0);
        double particleCount = Mth.clamp(player.getSpeed() * 20.0, 1, 10.0);

        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 0.8;
            double offsetZ = (Math.random() - 0.5) * 0.8;

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.SPLASH,
                    player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05 * splashPower);

            ((ServerLevel) player.level()).sendParticles(ParticleTypes.BUBBLE_POP,
                    player.getX() + offsetX, player.getY(), player.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05 * splashPower);
        }

        if (player.tickCount % 10 == 0) {
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.BOAT_PADDLE_WATER,
                    SoundSource.BLOCKS,
                    1.0F,
                    6.0F
            );
        }
    }

    public static void onBlindnessSpell(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:illusioner"))) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;
        if (source.is(DamageTypeTags.IS_PROJECTILE)) return;

        if (player.level().getRandom().nextInt(500) == 0) { //todo 1/5
            Level level = player.level();
            DustParticleOptions pinkDust = new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.7F), 1.0F);

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 30; i++) {
                    double offsetX = (Math.random() - 0.5) * 1.2;
                    double offsetY = Math.random() * 1.5;
                    double offsetZ = (Math.random() - 0.5) * 1.2;

                    serverLevel.sendParticles(
                            pinkDust,
                            attacker.getX() + offsetX,
                            attacker.getY() + offsetY + 0.5,
                            attacker.getZ() + offsetZ,
                            1, 0, 0, 0, 0.0
                    );
                }
            }

            Vec3 knockbackDir = attacker.position().subtract(player.position()).normalize();
            attacker.setDeltaMovement(knockbackDir.scale(0.8).add(0, 0.4, 0));
            attacker.hurtMarked = true;

            attacker.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));

            level.playSound(null, player.blockPosition(), SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.PLAYERS, 1.0f, 1.0f);
        }

    }

    public static void onSmokeBomb(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:illusioner"))) return;

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)) return;

        if (player.level().getRandom().nextInt(500) == 0) { //todo 1/5
            Level level = player.level();

            double distanceSq = player.distanceToSqr(attacker);
            if (distanceSq <= 4.0) {
                Vec3 knockback = attacker.position().subtract(player.position()).normalize().scale(0.8).add(0, 0.4, 0);
                attacker.setDeltaMovement(knockback);
                attacker.hurtMarked = true;
            }

            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 60; i++) {
                    double offsetX = (Math.random() - 0.5) * 2.0;
                    double offsetY = Math.random() * 2.0;
                    double offsetZ = (Math.random() - 0.5) * 2.0;

                    serverLevel.sendParticles(
                            ParticleTypes.POOF,
                            player.getX() + offsetX,
                            player.getY() + offsetY,
                            player.getZ() + offsetZ,
                            1, 0.0, 0.0, 0.0, 0.02
                    );
                }
            }

            if (attacker instanceof Mob mob) {
                mob.setTarget(null);
                mob.getNavigation().stop();
            }

            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0, false, false, true));

            level.playSound(null, player.blockPosition(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    public static void onDecoyEscape(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:illusioner"))) return;

        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (player.level().getRandom().nextInt(1) != 0) return;

        ServerLevel level = (ServerLevel) player.level();

        IllusionDecoyEntity decoy = ModEntities.ILLUSION_DECOY.get().create(level);

        if (decoy != null) {
            decoy.moveTo(player.getX(), player.getY(), player.getZ());

            ItemStack head = createPlayerHead(player);
            decoy.setItemSlot(EquipmentSlot.HEAD, head);

            level.addFreshEntity(decoy);
        }

        Vec3 look = player.getLookAngle().normalize().scale(-5);
        Vec3 tpPos = player.position().add(look);
        BlockPos pos = BlockPos.containing(tpPos);

        if (level.getBlockState(pos.below()).isSolid()) {
            player.teleportTo(tpPos.x, tpPos.y, tpPos.z);
        }
    }


    //WaterWalkMixin
}
