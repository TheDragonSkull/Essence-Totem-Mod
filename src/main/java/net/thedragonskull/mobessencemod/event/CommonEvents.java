package net.thedragonskull.mobessencemod.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.abilities.*;
import net.thedragonskull.mobessencemod.item.custom.TotemOfEssenceItem;
import net.thedragonskull.mobessencemod.network.C2SSwapTotemPacket;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.util.CommonAbilityUtils;
import net.thedragonskull.mobessencemod.util.KeyBindings;
import net.thedragonskull.mobessencemod.util.TotemUtils;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        ArmorStandAbility.stillPlayer(event);
        ShulkerAbility.onShulkerStill(event);
    }

    @SubscribeEvent
    public static void onPlayerEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof TotemOfEssenceItem totem)) return;
        if (!(event.getTarget() instanceof LivingEntity target)) return;

        InteractionResult result = totem.interactLivingEntity(stack, event.getEntity(), target, event.getHand());

        if (result != InteractionResult.PASS) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        PillagerAbility.onRideRavager(event);
        VindicatorAbility.onRideRavager(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        AllayAbility.onNoteblockUsed(event);
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        TadpoleAbility.onTadpoleSlimeSnack(event);
        PandaAbility.onPandaEat(event);
        IronGolemAbility.onUseIron(event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        BeeAbility.onPlayerHurt(event);
        CreeperAbility.onPlayerHurt(event);
        SlimeAbility.onPlayerHurt(event);
        MagmaCubeAbility.onPlayerHurt(event);
        SheepAbility.onPlayerHurt(event);
        WolfAbility.onPlayerHurt(event);
        SilverfishAbility.onPlayerHurt(event);
        EndermiteAbility.onPlayerHurt(event);
        SquidAbility.onPlayerHurt(event);
        GlowSquidAbility.onPlayerHurt(event);
        HorseAbility.horseKick(event);
        DonkeyAbility.donkeyKick(event);
        MuleAbility.muleKick(event);
        ZombieHorseAbility.zombieHorseKick(event);
        SkeletonHorseAbility.skeletonHorseKick(event);
        WitchAbility.witchPotion(event);
        TurtleAbility.turtleBlock(event);
        GuardianAbility.guardianDamageReduction(event);
        ElderGuardianAbility.elderGuardianDamageReduction(event);
        StriderAbility.onFireHurt(event);
        CommonAbilityUtils.onCatLand(event);
        RavagerAbility.onRavagerRoar(event);
        EvokerAbility.onPlayerHurt(event);
        PillagerAbility.onPillagerShoot(event);
        VindicatorAbility.onVindicatorAttack(event);
        GhastAbility.onGhastFireball(event);
        PandaAbility.onPandaHit(event);
        ShulkerAbility.onShulkerTank(event);
        IronGolemAbility.onGolemDefense(event);
        WardenAbility.onPlayerHurt(event);
        WitherSkeletonAbility.onWitherAndFireHurt(event);
        ZombifiedPiglinAbility.onZombifiedPiglinSummon(event);
    }

    @SubscribeEvent
    public static void onLivingUseItem(LivingEntityUseItemEvent.Finish event) {
        PigAbility.onItemEaten(event);
        RedMooshroomAbility.onEat(event);
        BrownMooshroomAbility.onEat(event);
        PandaAbility.onPandaEatVegetable(event);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        ZombieAbility.onPlayerDeath(event);
        ZombieVillagerAbility.onPlayerDeath(event);
        ZombieHorseAbility.onPlayerDeath(event);
        HuskAbility.onPlayerDeath(event);
        AxolotlAbility.onPlayerKill(event);
        RabbitAbility.onRabbitFrenzy(event);
        WardenAbility.onKillEntity(event);
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        CaveSpiderAbility.caveSpiderPoison(event);
        EndermanAbility.teleport(event);
        BlazeAbility.blazeSetOnFire(event);
        DrownedAbility.preventLethalDrowningDamage(event);
        CamelAbility.onCactusHurt(event);
        PolarBearAbility.bearKnockback(event);
        CommonAbilityUtils.onFreezingHurt(event);
        FrogAbility.onFrogImmunity(event);
        ShulkerAbility.shulkerHurt(event);
        IronGolemAbility.onGolemAttack(event);
        WitherSkeletonAbility.onApplyWither(event);
        PiglinBruteAbility.onAttack(event);
        ZombifiedPiglinAbility.onLightningStrikeHurt(event);
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        PiglinAbility.onPiglinAttack(event);
        PiglinBruteAbility.onBruteDoubleDamage(event);
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        RavagerAbility.onRavagerKnockback(event);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        VexAbility.onVexAttack(event);
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        SpiderAbility.climb(event);
        CaveSpiderAbility.climb(event);
        ChickenAbility.slowFall(event);
        SalmonAbility.swimBoost(event);
        PufferfishAbility.applyPoisonOnContact(event);
        GoatAbility.applyRamAttack(event);
        BlazeAbility.blazeLevitate(event);
        MagmaCubeAbility.setOnFireOnContact(event);
        PhantomAbility.onPlayerPhantomFly(event);
        TraderLlamaAbility.onPlayerTick(event);
        DolphinAbility.swimBoost(event);
        DolphinAbility.dolphinAutoSpinDash(event);
        GlowSquidAbility.followGlowSquid(event);
        HorseAbility.horseJump(event);
        MuleAbility.muleJump(event);
        ZombieHorseAbility.zombieHorseJump(event);
        SkeletonHorseAbility.skeletonHorseJump(event);
        HuskAbility.preventStarvation(event);
        GuardianAbility.guardianFocusTick(event);
        ElderGuardianAbility.elderGuardianFocusTick(event);
        CamelAbility.camelStep(event);
        PolarBearAbility.bearResistance(event);
        VexAbility.onTraverseBlock(event);
        RavagerAbility.applyRavagerRamAttack(event);
        VindicatorAbility.vindicatorAxeSpeed(event);
        IronGolemAbility.onPlayerTick(event);
        WardenAbility.onPlayerTick(event);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        TropicalFishAbility.onRenderFog(event);
        StriderAbility.onRenderFog(event);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        TropicalFishAbility.onFogColor(event);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        FrogAbility.onFrogFallDamage(event);
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        FrogAbility.onFrogJump(event);
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        StrayAbility.straySlowArrow(event);
        CommonAbilityUtils.onArrowLoose(event);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        LlamaAbility.onLlamaSpitImpact(event);
        TraderLlamaAbility.onTraderLlamaSpitImpact(event);
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        CatAbility.onPlayerWakeUp(event);
    }

    @SubscribeEvent
    public static void onFoxLoot(LivingDropsEvent event) {
        CommonAbilityUtils.onFoxLoot(event);
        TotemUtils.onMobDrops(event);
    }

        @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        WolfAbility.onServerTick(event);
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        BeeAbility.onBeeTarget(event);
        WanderingTraderAbility.onLlamaTarget(event);
        PhantomAbility.onPhantomTarget(event);
        SilverfishAbility.onSilverfishTarget(event);
        EndermiteAbility.onEndermiteTarget(event);
        ArmorStandAbility.onMobTarget(event);
        CommonAbilityUtils.onCreeperTarget(event);
        EvokerAbility.onVexTarget(event);
        CommonAbilityUtils.onRavagerTarget(event);
    }

    @SubscribeEvent
    public static void onInputKeyEvent(InputEvent.Key event) {
        ParrotAbility.flap(event);
        CamelAbility.camelDash(event);

        if (KeyBindings.INSTANCE.SWAP_TOTEM.consumeClick()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                PacketHandler.sendToServer(new C2SSwapTotemPacket());
            }
        }

    }

    @SubscribeEvent
    public static void renderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        IronGolemAbility.onOverlayRender(event);
    }

    @SubscribeEvent
    public static void onTotemTransform(EntityStruckByLightningEvent event) {
        PigAbility.onTotemTransform(event);
    }

        private static final ResourceLocation FRAME = ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "textures/gui/totem_frame.png");

    @SubscribeEvent
    public static void renderTotemIndicator(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id() != VanillaGuiOverlay.EXPERIENCE_BAR.id()) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        ItemStack totem = TotemUtils.getVisibleTotemStack(player);
        if (totem == null) return;

        int screenWidth = event.getWindow().getGuiScaledWidth();
        int screenHeight = event.getWindow().getGuiScaledHeight();

        int x = screenWidth / 2 + 100;
        int y = screenHeight - 19;

        guiGraphics.blit(FRAME, x - 3, y - 3, 0, 0, 22, 22, 22, 22);
        guiGraphics.renderItem(totem, x, y);
    }

}
