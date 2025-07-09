package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.List;

public class CatAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cat"))) return;

        if (event.updateLevel()) return; // todo: test multiplayer

        List<MobEffect> effects = BuiltInRegistries.MOB_EFFECT.stream()
                .filter(effect -> effect.isBeneficial() && !effect.isInstantenous() && effect != MobEffects.HERO_OF_THE_VILLAGE)
                .toList();

        if (!effects.isEmpty()) {
            MobEffect randomEffect = Util.getRandom(effects, player.getRandom());
            player.addEffect(new MobEffectInstance(randomEffect, 20 * 120, 0));
        }
    }


    //AbilityUtils.onCatLand;
    //AbilityUtils.onCreeperTarget;
}
