package net.thedragonskull.mobessencemod.event;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.render.*;
import net.thedragonskull.mobessencemod.util.KeyBindings;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEvents {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {

        for (String skinType : event.getSkins()) {
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skinType);

            if (renderer instanceof PlayerRenderer playerRenderer) {
                ModelPart villagerNosePart = event.getEntityModels().bakeLayer(VillagerNoseModel.LAYER_LOCATION);
                ModelPart witchNosePart = event.getEntityModels().bakeLayer(WitchNoseModel.LAYER_LOCATION);
                playerRenderer.addLayer(new VillagerNoseLayer(renderer, villagerNosePart, witchNosePart));

                playerRenderer.addLayer(new FractureOverlayLayer(playerRenderer));
                playerRenderer.addLayer(new PlayerEnergyArmorLayer(renderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(VillagerNoseModel.LAYER_LOCATION, VillagerNoseModel::createBodyLayer);
        event.registerLayerDefinition(WitchNoseModel.LAYER_LOCATION, WitchNoseModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.INSTANCE.SWAP_TOTEM);
    }

}
