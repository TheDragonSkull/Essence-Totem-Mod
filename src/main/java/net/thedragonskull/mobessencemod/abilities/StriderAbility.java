package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class StriderAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onFireHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:strider"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypeTags.IS_FIRE)) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    public static void onRenderFog(ViewportEvent.RenderFog event) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || !player.isEyeInFluidType(ForgeMod.LAVA_TYPE.get())) return;
        if (!TotemUtils.hasTotemWithEssenceClient(player, ResourceLocation.parse("minecraft:strider"))) return;

        event.setCanceled(true);
        event.setNearPlaneDistance(0.0f);
        event.setFarPlaneDistance(40.0f);
    }

}
