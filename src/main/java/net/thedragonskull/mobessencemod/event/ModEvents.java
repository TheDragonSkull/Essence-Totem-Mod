package net.thedragonskull.mobessencemod.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.entity.ModEntities;
import net.thedragonskull.mobessencemod.entity.custom.IllusionDecoyEntity;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ILLUSION_DECOY.get(), IllusionDecoyEntity.createAttributes().build());
    }
}
