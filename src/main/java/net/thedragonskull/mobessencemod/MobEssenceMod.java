package net.thedragonskull.mobessencemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.thedragonskull.mobessencemod.block.ModBlocks;
import net.thedragonskull.mobessencemod.block.entity.ModBlockEntities;
import net.thedragonskull.mobessencemod.entity.ModEntities;
import net.thedragonskull.mobessencemod.item.ModCreativeModeTabs;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.item.custom.TotemOfEssenceItem;
import net.thedragonskull.mobessencemod.loot.ModLootModifiers;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.particle.ModParticles;
import net.thedragonskull.mobessencemod.render.CrownRenderer;
import net.thedragonskull.mobessencemod.sound.ModSounds;
import net.thedragonskull.mobessencemod.util.ModItemProperties;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(MobEssenceMod.MOD_ID)
public class MobEssenceMod {
    public static final String MOD_ID = "mobessencemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MobEssenceMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            EntityRenderers.register(ModEntities.ILLUSION_DECOY.get(), ArmorStandRenderer::new);

            event.enqueueWork(() -> {
                event.enqueueWork(ModItemProperties::addCustomItemProperties);
                event.enqueueWork(PacketHandler::register);

                // Curios
                ModItems.ITEMS.getEntries().stream()
                        .map(RegistryObject::get)
                        .filter(item -> item instanceof TotemOfEssenceItem)
                        .forEach(item -> CuriosRendererRegistry.register(item, CrownRenderer::new));
            });
        }


    }
}
