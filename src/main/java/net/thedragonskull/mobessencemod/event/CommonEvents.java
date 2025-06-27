package net.thedragonskull.mobessencemod.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.abilities.*;
import net.thedragonskull.mobessencemod.util.TotemUtils;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        BeeAbility.onPlayerHurt(event);
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        EndermanAbility.onPlayerHurt(event);
    }

    @SubscribeEvent
    public static void onLivingUseItem(LivingEntityUseItemEvent.Finish event) {
        PigAbility.onItemEaten(event);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        ZombieAbility.onPlayerDeath(event);
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        CaveSpiderAbility.onAttack(event);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        SpiderAbility.climb(event);
        CaveSpiderAbility.climb(event);
        ParrotAbility.slowFall(event);
        SalmonAbility.swimBoost(event);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        TropicalFishAbility.onRenderFog(event);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        TropicalFishAbility.onFogColor(event);
    }

}
