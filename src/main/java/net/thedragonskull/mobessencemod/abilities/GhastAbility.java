package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class GhastAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onGhastFireball(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ghast"))) return;

        DamageSource source = event.getSource();
        Entity projectile = source.getDirectEntity();
        Entity attacker = source.getEntity();

        if (!(projectile instanceof Projectile)) return;
        if (!(attacker instanceof LivingEntity target)) return;
        if (attacker.is(player)) return;

        if (player.getRandom().nextInt(3) != 0) return;

        launchFireCharge(player, target);

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
    }

    private static void launchFireCharge(ServerPlayer player, LivingEntity target) {
        Level level = player.level();

        Vec3 dir = target.position().add(0, target.getBbHeight() / 2.0, 0)
                .subtract(player.getEyePosition()).normalize();

        Fireball fireCharge = new LargeFireball(level, player, dir.x, dir.y, dir.z, 1);

        Vec3 spawnPos = player.getEyePosition().add(dir.scale(1.5));
        fireCharge.setPos(spawnPos);

        level.addFreshEntity(fireCharge);

        level.playSound(null, player.blockPosition(), SoundEvents.GHAST_WARN, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, player.blockPosition(), SoundEvents.GHAST_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
    }


}
