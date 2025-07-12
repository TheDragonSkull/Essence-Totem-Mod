package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

public class TadpoleAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    private static final List<EffectData> LAND_EFFECTS = List.of(
            new EffectData(MobEffects.DAMAGE_RESISTANCE, 20 * 20, 0),
            new EffectData(MobEffects.MOVEMENT_SPEED, 20 * 20, 0),
            new EffectData(MobEffects.ABSORPTION, 20 * 20, 0),
            new EffectData(MobEffects.REGENERATION, 20 * 20, 0),
            new EffectData(MobEffects.HEAL, 1, 0)
    );

    private static final List<EffectData> WATER_EFFECTS = List.of(
            new EffectData(MobEffects.REGENERATION, 20 * 20, 0),
            new EffectData(MobEffects.WATER_BREATHING, 20 * 20, 0),
            new EffectData(MobEffects.DOLPHINS_GRACE, 20 * 20, 0),
            new EffectData(MobEffects.HEAL, 1, 0)
    );

    private record EffectData(MobEffect effect, int duration, int amplifier) {}

    public static void onTadpoleSlimeSnack(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) return;
        Level level = player.level();
        ItemStack item = event.getItemStack();

        if (!TotemUtils.hasTotemWithEssenceServer(serverPlayer, ResourceLocation.parse("minecraft:tadpole"))) return;

        if (!item.is(Items.SLIME_BALL)) return;

        item.shrink(1);
        event.setCanceled(true);

        List<EffectData> pool = player.isInWater() ? WATER_EFFECTS : LAND_EFFECTS;
        EffectData chosen = pool.get(player.getRandom().nextInt(pool.size()));
        MobEffectInstance effect = new MobEffectInstance(chosen.effect(), chosen.duration(), chosen.amplifier(), false, false, true);
        player.addEffect(effect);

        player.getCooldowns().addCooldown(item.getItem(), 20 * 20);

        level.playSound(null, player.blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.PLAYERS, 1.0F, 1.2F);

        ServerLevel serverLevel = (ServerLevel) event.getLevel();
        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX(), player.getY() + 1.0, player.getZ(),
                10, 0.3, 0.3, 0.3, 0.1);
    }
}
