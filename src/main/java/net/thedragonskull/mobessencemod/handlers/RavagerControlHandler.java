package net.thedragonskull.mobessencemod.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.util.TotemUtils;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class RavagerControlHandler {

    private static final String MOUNTED_TAG = "MobEssence_MountedByPlayer";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;

        ServerPlayer player = (ServerPlayer) event.player;

        if (player.getVehicle() instanceof Ravager ravager) {
            if (TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:pillager")) ||
                    TotemUtils.hasTotemWithEssenceServer(player, ResourceLocation.parse("minecraft:vindicator"))) {

                CompoundTag data = ravager.getPersistentData();

                if (!ravager.isNoAi() && !data.getBoolean(MOUNTED_TAG)) {
                    data.putBoolean(MOUNTED_TAG, true);
                    ravager.setNoAi(true);
                }

                ravager.setYRot(player.getYRot());
                ravager.yRotO = ravager.getYRot();
                ravager.setXRot(player.getXRot() * 0.5F);
                ravager.xRotO = ravager.getXRot();
                ravager.yBodyRot = ravager.getYRot();
                ravager.yHeadRot = ravager.getYRot();

                float forward = player.zza;
                float strafe = player.xxa;
                float speed = 0.15F;

                Vec3 motion = new Vec3(strafe, 0, forward);
                ravager.setSpeed(speed);
                ravager.moveRelative(speed, motion);
                ravager.move(MoverType.SELF, ravager.getDeltaMovement());
                ravager.setDeltaMovement(Vec3.ZERO);
            } else {
                CompoundTag data = ravager.getPersistentData();
                if (ravager.isNoAi() && data.getBoolean(MOUNTED_TAG)) {
                    ravager.setNoAi(false);
                    data.remove(MOUNTED_TAG);
                }
            }
        }
        
        if (player.getVehicle() == null) {
            for (Entity nearby : player.level().getEntities(player, player.getBoundingBox().inflate(4), e -> e instanceof Ravager)) {
                Ravager nearbyRavager = (Ravager) nearby;
                CompoundTag data = nearbyRavager.getPersistentData();

                if (nearbyRavager.isNoAi()
                        && nearbyRavager.getControllingPassenger() == null
                        && data.getBoolean(MOUNTED_TAG)) {

                    nearbyRavager.setNoAi(false);
                    data.remove(MOUNTED_TAG);
                }
            }
        }
    }


}

