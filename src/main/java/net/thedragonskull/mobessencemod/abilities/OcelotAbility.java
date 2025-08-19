package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class OcelotAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void onMobTarget(LivingChangeTargetEvent event) {
        if (!(event.getNewTarget() instanceof Player player)) return;
        if (!(event.getEntity() instanceof Mob mob)) return;

        if (!TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:ocelot"))) return;

        if (!player.isCrouching()) return;

        double baseRange = 16.0;
        var inst = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (inst != null) baseRange = inst.getValue();

        double effectiveRange = Math.max(0, baseRange - 8);

        if (mob.distanceTo(player) > effectiveRange) {
            event.setNewTarget(null);
        }
    }

    //CommonAbilityUtils.onCatLand;
    //CommonAbilityUtils.onCreeperTarget;
    //CreeperMixin
}
