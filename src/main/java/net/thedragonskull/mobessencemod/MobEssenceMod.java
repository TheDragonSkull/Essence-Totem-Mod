package net.thedragonskull.mobessencemod;

import com.mojang.logging.LogUtils;
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
import net.thedragonskull.mobessencemod.block.ModBlocks;
import net.thedragonskull.mobessencemod.block.entity.ModBlockEntities;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.loot.ModLootModifiers;
import net.thedragonskull.mobessencemod.network.PacketHandler;
import net.thedragonskull.mobessencemod.particle.ModParticles;
import net.thedragonskull.mobessencemod.sound.ModSounds;
import net.thedragonskull.mobessencemod.util.ModItemProperties;
import org.slf4j.Logger;

@Mod(MobEssenceMod.MOD_ID)
public class MobEssenceMod {
    public static final String MOD_ID = "mobessencemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MobEssenceMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModSounds.register(modEventBus);
        ModParticles.register(modEventBus);

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
            event.enqueueWork(() -> {
                event.enqueueWork(ModItemProperties::addCustomItemProperties);
                event.enqueueWork(PacketHandler::register);
            });
        }


    }
}
