package net.thedragonskull.mobessencemod.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class MobEssenceCapabilities {
    public static Capability<IMobEssenceData> MOB_ESSENCE_CAP = CapabilityManager.get(new CapabilityToken<>() {});
}
