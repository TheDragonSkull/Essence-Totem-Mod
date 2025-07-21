package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class WitherAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onUseWitherSkull(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wither"))) return;

        ItemStack item = event.getItemStack();
        if (!item.is(Items.WITHER_SKELETON_SKULL)) return;

        ServerLevel level = (ServerLevel) player.level();

        boolean isBlue = player.getRandom().nextInt(5) == 0;

        WitherSkull skull = new CustomWitherSkull(level, player, isBlue);
        skull.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
        skull.setOwner(player);
        skull.setDangerous(isBlue);

        Vec3 look = player.getLookAngle().normalize();
        skull.setDeltaMovement(look.scale(isBlue ? 1.4 : 0.8));

        level.addFreshEntity(skull);

        player.getCooldowns().addCooldown(Items.WITHER_SKELETON_SKULL, 600);

        level.playSound(null, player.blockPosition(),
                SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 1.0f, isBlue ? 0.5f : 1.0f);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void onAbstractArrowHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wither"))) return;

        DamageSource source = event.getSource();

        if (player.getHealth() <= player.getMaxHealth() / 2.0F && source.getDirectEntity() instanceof AbstractArrow) {
            event.setCanceled(true);
        }
    }

    public static void onWitherEffect(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:wither")))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.WITHER)) {
            event.setCanceled(true);
        }
    }

    //CancelEffectMixin

    private static class CustomWitherSkull extends WitherSkull {

        private int lifetime = 0;
        private final boolean isBlue;

        public CustomWitherSkull(Level level, LivingEntity owner, boolean isBlue) {
            super(level, owner, 0, 0, 0);
            this.isBlue = isBlue;
        }

        @Override
        public void tick() {
            super.tick();
            lifetime++;
            if (lifetime > 100 && !level().isClientSide) {
                this.discard();

                float power = isBlue ? 1.5F : 1.0F;
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), power, false, Level.ExplosionInteraction.NONE);

                AABB area = new AABB(this.blockPosition()).inflate(power + 1.0);
                List<LivingEntity> affected = this.level().getEntitiesOfClass(LivingEntity.class, area);

                int amplifier = isBlue ? 1 : 0;
                for (LivingEntity entity : affected) {
                    if (entity.isAlive() && !entity.isAlliedTo(this.getOwner())) {
                        entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, amplifier), this.getEffectSource());
                    }
                }
            }
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {
            if (!this.level().isClientSide) {
                Entity target = result.getEntity();

                float damage = isBlue ? 10.0F : 6.0F;

                boolean didDamage = target.hurt(this.damageSources().magic(), damage);

                if (didDamage && target instanceof LivingEntity livingTarget) {
                    livingTarget.addEffect(
                            new MobEffectInstance(MobEffects.WITHER, 100, 0),
                            this.getEffectSource()
                    );
                }

                this.discard();
            }
        }
    }

}
