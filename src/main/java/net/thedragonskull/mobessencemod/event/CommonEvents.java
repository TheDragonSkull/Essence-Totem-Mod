package net.thedragonskull.mobessencemod.event;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.abilities.BeeAbility;
import net.thedragonskull.mobessencemod.abilities.PigAbility;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class CommonEvents {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        BeeAbility.onPlayerHurt(event);
    }

    @SubscribeEvent
    public static void onLivingUseItem(LivingEntityUseItemEvent.Finish event) {
        PigAbility.onItemEaten(event);
    }

}
