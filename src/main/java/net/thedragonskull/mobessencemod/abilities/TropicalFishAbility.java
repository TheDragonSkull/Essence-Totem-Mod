package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ViewportEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class TropicalFishAbility implements IMobAbility {

    private static final ResourceLocation ESSENCE_ID = ResourceLocation.parse("minecraft:tropical_fish");

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onRenderFog(ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.isUnderWater()) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ESSENCE_ID)) return;

        event.setCanceled(true);
        event.setNearPlaneDistance(0.0f);
        event.setFarPlaneDistance(180.0f);
    }

    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.isUnderWater()) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ESSENCE_ID)) return;

        event.setRed(0.5f);
        event.setGreen(0.7f);
        event.setBlue(0.8f);
    }

}
