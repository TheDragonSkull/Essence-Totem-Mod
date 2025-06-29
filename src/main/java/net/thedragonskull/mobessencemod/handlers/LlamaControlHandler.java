package net.thedragonskull.mobessencemod.handlers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thedragonskull.mobessencemod.MobEssenceMod;
import net.thedragonskull.mobessencemod.util.TotemUtils;

@Mod.EventBusSubscriber(modid = MobEssenceMod.MOD_ID)
public class LlamaControlHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (event.player.level().isClientSide()) return;

        Player player = event.player;

        if (!(player.getVehicle() instanceof Llama llama)) return;

        if (!TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:wandering_trader")) &&
                !TotemUtils.hasTotemWithEssenceServer((ServerPlayer) player, ResourceLocation.parse("minecraft:trader_llama"))) return;

        llama.setYRot(player.getYRot());
        llama.yRotO = llama.getYRot();
        llama.setXRot(player.getXRot() * 0.5F);
        llama.xRotO = llama.getXRot();
        llama.yBodyRot = llama.getYRot();
        llama.yHeadRot = llama.getYRot();

        float forward = player.zza;
        float strafe = player.xxa;
        float speed = 0.3F;

        Vec3 motion = new Vec3(strafe, 0, forward);
        llama.setSpeed(speed);
        llama.moveRelative(speed, motion);
        llama.move(MoverType.SELF, llama.getDeltaMovement());
        llama.setDeltaMovement(Vec3.ZERO);
    }

}

