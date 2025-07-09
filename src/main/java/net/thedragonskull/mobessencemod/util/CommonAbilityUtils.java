package net.thedragonskull.mobessencemod.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import static net.thedragonskull.mobessencemod.util.TotemUtils.hasTotemWithEssenceServer;

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

    // FOX & SNOW FOX
    public static void onFoxLoot(LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof ServerPlayer player)) return;

        if (!(TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:fox")) ||
                TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:snow_fox")))) return;

        Level world = player.level();
        if (world.isDay()) return;

        if (player.getRandom().nextFloat() < 0.25F) {
            for (ItemEntity drop : event.getDrops()) {
                ItemStack extra = drop.getItem().copy();
                world.addFreshEntity(new ItemEntity(world, drop.getX(), drop.getY(), drop.getZ(), extra));
            }
        }
    }

    // POLAR BEAR & STRAY
    public static void onFreezingHurt(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:polar_bear"))) return;

        DamageSource source = event.getSource();

        if (source.is(DamageTypes.FREEZE)) {
            event.setCanceled(true);
        }
    }
}
