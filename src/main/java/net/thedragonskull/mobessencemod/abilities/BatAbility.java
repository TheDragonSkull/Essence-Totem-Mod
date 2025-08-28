package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BatAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack stack) {
        if (player == null) return;

        if (!player.isCrouching()) return;

        List<LivingEntity> nearbyEnemies = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(15),
                entity -> (entity instanceof Mob || entity instanceof Player) && entity != player);

        for (LivingEntity enemy : nearbyEnemies) {
            enemy.addEffect(new MobEffectInstance(MobEffects.GLOWING, 2, 0, false, false));
        }

        if (!player.level().isDay()) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false, false));
        }
    }
}
