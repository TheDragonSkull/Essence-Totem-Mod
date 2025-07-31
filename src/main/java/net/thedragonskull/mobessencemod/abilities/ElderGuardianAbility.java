package net.thedragonskull.mobessencemod.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElderGuardianAbility implements IMobAbility {

    private static final Map<UUID, UUID> focusedMobs = new HashMap<>();

    @Override
    public void tick(ServerPlayer player, ItemStack totemStack) {
    }

    public static void elderGuardianFocusTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:elder_guardian"))) return;

        LivingEntity lookedMob = getEntityLookedAt(player, 10.0);

        if (lookedMob != null) {
            focusedMobs.put(player.getUUID(), lookedMob.getUUID());
        } else {
            focusedMobs.remove(player.getUUID());
        }

    }

    public static LivingEntity getEntityLookedAt(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle().normalize();
        Vec3 reach = eyePos.add(lookVec.scale(range));

        AABB box = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0);
        Level level = player.level();

        return level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive()).stream()
                .filter(entity -> {
                    AABB bounds = entity.getBoundingBox().inflate(0.25);
                    return bounds.clip(eyePos, reach).isPresent();
                })
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(player)))
                .orElse(null);
    }

    public static void elderGuardianDamageReduction(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:elder_guardian"))) return;

        Entity sourceEntity = event.getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity mob)) return;

        UUID focusedMobId = focusedMobs.get(player.getUUID());
        if (focusedMobId == null) return;

        if (!mob.getUUID().equals(focusedMobId)) return;

        float reduction = player.isUnderWater() ? 0.5f : 0.75f;
        event.setAmount(event.getAmount() * reduction);
    }

    //NoUnderwaterMiningPenaltyMixin
}
