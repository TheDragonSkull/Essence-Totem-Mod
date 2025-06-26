package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class CodAbility implements IMobAbility {

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
        if (player.isUnderWater() && !player.isCreative() && !player.isSpectator()) {
            int air = player.getAirSupply();

            if (player.tickCount % 2 == 0 && air < player.getMaxAirSupply()) {
                player.setAirSupply(air + 1);
            }
        }
    }

}
