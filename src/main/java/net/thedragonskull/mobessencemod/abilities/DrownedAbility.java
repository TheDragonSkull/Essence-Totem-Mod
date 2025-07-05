package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

public class DrownedAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void preventLethalDrowningDamage(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:drowned"))) return;

        DamageSource source = event.getSource();
        boolean drowning = source.is(DamageTypeTags.IS_DROWNING);
        boolean suffocation = source.is(DamageTypes.IN_WALL);

        if (!(drowning || suffocation)) return;

        float health = player.getHealth();
        float incomingDamage = 2.0F;

        if (health <= 1.0F) {
            event.setCanceled(true);
        }

        if (health - incomingDamage <= 1.0F) {
            event.setCanceled(true);
        }

    }


}
