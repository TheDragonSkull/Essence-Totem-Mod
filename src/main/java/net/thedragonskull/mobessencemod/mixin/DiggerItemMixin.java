package net.thedragonskull.mobessencemod.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.thedragonskull.mobessencemod.util.TotemUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
public class DiggerItemMixin {

    @Inject(method = "hurtEnemy", at = @At("HEAD"), cancellable = true)
    private void mobessencemod$maybeBypassDurability(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        if (!(attacker instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:piglin_brute"))) return;

        if (!stack.is(Items.GOLDEN_AXE)) return;

        if (player.getRandom().nextFloat() < 0.5f) {
            cir.setReturnValue(true);
        }
    }
}
