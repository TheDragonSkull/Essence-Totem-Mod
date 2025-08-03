package net.thedragonskull.mobessencemod.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.item.ModItems;
import net.thedragonskull.mobessencemod.util.TotemUtils;

import java.util.*;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class IllusionerTotemRitualHandler {

    private static final int MAX_BOOKSHELVES = 12;
    private static final int REQUIRED_TICKS = 100;

    private static final Map<UUID, Integer> validItemEntities = new HashMap<>();

    @SubscribeEvent
    public static void onIllusionerRitual(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            List<ItemEntity> candidates = level.getEntitiesOfClass(ItemEntity.class,
                    new AABB(new BlockPos(-1000, 0, -1000), new BlockPos(1000, 256, 1000)),
                    entity -> isIllagerTotem(entity.getItem())
            );

            Set<UUID> seenThisTick = new HashSet<>();

            for (ItemEntity item : candidates) {
                BlockPos pos = item.blockPosition();
                Vec3 itemPos = item.position().add(0, 0.25, 0);

                int beamRadius = 3;
                BlockPos.betweenClosedStream(pos.offset(-beamRadius, -1, -beamRadius), pos.offset(beamRadius, 2, beamRadius))
                        .forEach(check -> {
                            if (level.getBlockState(check).is(Blocks.BOOKSHELF)) {
                                Vec3 shelfPos = new Vec3(check.getX() + 0.5, check.getY() + 0.75, check.getZ() + 0.5);
                                spawnParticleBeam(level, shelfPos, itemPos, ParticleTypes.ENCHANT, 12);
                            }
                        });

                int logicRadius = 3;
                int bookshelves = countNearbyBookshelves(level, pos, logicRadius);
                if (bookshelves >= MAX_BOOKSHELVES) {
                    seenThisTick.add(item.getUUID());
                    int ticks = validItemEntities.compute(item.getUUID(), (uuid, val) -> val == null ? 1 : val + 1);

                    if (ticks >= REQUIRED_TICKS) {

                        level.addFreshEntity(new LightningBolt(EntityType.LIGHTNING_BOLT, level) {{
                            setPos(item.getX(), item.getY(), item.getZ());
                            setVisualOnly(true);
                        }});

                        ItemStack original = item.getItem();
                        ItemStack modified = original.copy();
                        TotemUtils.setEssence(modified, ResourceLocation.parse("minecraft:illusioner"));
                        item.setItem(modified);
                        validItemEntities.remove(item.getUUID());
                    }
                }
            }

            Map<UUID, Integer> fallbackCounters = new HashMap<>();

            for (UUID uuid : new HashSet<>(validItemEntities.keySet())) {
                if (!seenThisTick.contains(uuid)) {
                    int fallback = fallbackCounters.merge(uuid, 1, Integer::sum);
                    if (fallback >= 20) {
                        validItemEntities.remove(uuid);
                        fallbackCounters.remove(uuid);
                    }
                } else {
                    fallbackCounters.remove(uuid);
                }
            }
        }
    }

    private static int countNearbyBookshelves(Level level, BlockPos center, int radius) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, 2, radius))) {
            if (level.getBlockState(pos).is(Blocks.BOOKSHELF)) {
                count++;
                if (count >= MAX_BOOKSHELVES) break;
            }
        }
        return count;
    }

    private static final Set<ResourceLocation> ILLAGER_ESSENCES = Set.of(
            ResourceLocation.parse("minecraft:evoker"),
            ResourceLocation.parse("minecraft:pillager"),
            ResourceLocation.parse("minecraft:vindicator")
    );

    private static boolean isIllagerTotem(ItemStack stack) {
        if (stack.getItem() != ModItems.TOTEM_OF_ESSENCE.get()) return false;
        ResourceLocation essence = TotemUtils.getEssence(stack);
        return ILLAGER_ESSENCES.contains(essence);
    }

    private static void spawnParticleBeam(ServerLevel level, Vec3 start, Vec3 end, ParticleOptions particle, int steps) {
        Vec3 direction = end.subtract(start);
        double distance = direction.length();

        if (distance < 0.2) {
            Vec3 mid = start.add(direction.scale(0.5));
            level.sendParticles(particle, mid.x, mid.y, mid.z, 1, 0, 0, 0, 0);
            return;
        }

        direction = direction.normalize();

        for (int i = 0; i < steps; i++) {
            double progress = (i / (double) steps) * distance;
            Vec3 point = start.add(direction.scale(progress));
            level.sendParticles(particle, point.x, point.y, point.z, 1, 0, 0, 0, 0);
        }
    }
}
