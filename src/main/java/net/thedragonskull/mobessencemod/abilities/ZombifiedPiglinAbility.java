package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

public class ZombifiedPiglinAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onZombifiedPiglinSummon(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombified_piglin"))) return;

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) return;

        if (player.level().random.nextInt(12) != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        ZombifiedPiglin piglin = EntityType.ZOMBIFIED_PIGLIN.create(level);
        if (piglin == null) return;

        BlockPos spawnPos = attacker.blockPosition().offset(level.random.nextInt(5) - 1, 0, level.random.nextInt(3) - 1);
        piglin.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, player.getYRot(), 0.0F);

        piglin.setTarget(attacker);
        piglin.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        piglin.skipDropExperience();
        piglin.getPersistentData().putBoolean("MobEssenceSummoned", true);

        level.addFreshEntity(piglin);

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(piglin.position());
            lightning.setVisualOnly(true);
            level.addFreshEntity(lightning);
        }

        player.displayClientMessage(Component.literal("A vengeful soul rises.").withStyle(ChatFormatting.DARK_RED), true);
        level.playSound(null, piglin.blockPosition(), SoundEvents.ZOMBIFIED_PIGLIN_ANGRY, SoundSource.PLAYERS, 1.5f, 1.0f);
    }

    public static void onLightningStrikeHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:zombified_piglin")))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.LIGHTNING_BOLT)) {
            event.setCanceled(true);
        }
    }
}
