package net.thedragonskull.mobessencemod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class CommonAbilityUtils {

    // CAT & OCELOT
    public static void onCatLand(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:ocelot")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:cat")))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FALL) && player.isShiftKeyDown()) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    public static void onCreeperTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Creeper)) return;
        if (!(event.getNewTarget() instanceof Player player)) return;

        if (TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:ocelot")) ||
                TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:cat"))) {
            event.setCanceled(true);
        }
    }

}
