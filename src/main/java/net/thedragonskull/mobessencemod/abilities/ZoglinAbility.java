package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class ZoglinAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onZoglinSummon(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zoglin"))) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        if (player.level().random.nextInt(12) != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        Zoglin zoglin = EntityType.ZOGLIN.create(level);
        if (zoglin == null) return;

        BlockPos spawnPos = attacker.blockPosition().offset(level.random.nextInt(5) - 1, 0, level.random.nextInt(3) - 1);
        zoglin.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), 0.0F);

        zoglin.setTarget(attacker);
        zoglin.skipDropExperience();
        zoglin.getPersistentData().putBoolean("MobEssenceSummoned", true);

        level.addFreshEntity(zoglin);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(zoglin.position());
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }

        player.displayClientMessage(Component.literal("A vengeful soul rises.").withStyle(ChatFormatting.DARK_RED), true);
        level.playSound(null, zoglin.blockPosition(), SoundEvents.ZOGLIN_ANGRY, SoundSource.PLAYERS, 1.5f, 1.0f);
    }

    // CommonAbilityUtils.onHoglinKnockback
    // CommonAbilityUtils.onHoglinAttack
    // ZoglinMixin
}
