package net.thedragonskull.mobessencemod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.thedragonskull.mobessencemod.MobEssenceMod;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MobEssenceMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int id = 0;

    public static void register() {

        INSTANCE.messageBuilder(C2SFlapSoundPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SFlapSoundPacket::encode)
                .decoder(C2SFlapSoundPacket::new)
                .consumerMainThread(C2SFlapSoundPacket::handle)
                .add();

        INSTANCE.messageBuilder(C2SParrotFlapPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SParrotFlapPacket::encode)
                .decoder(C2SParrotFlapPacket::new)
                .consumerMainThread(C2SParrotFlapPacket::handle)
                .add();

        INSTANCE.messageBuilder(C2SSwapTotemPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SSwapTotemPacket::encode)
                .decoder(C2SSwapTotemPacket::new)
                .consumerMainThread(C2SSwapTotemPacket::handle)
                .add();

        INSTANCE.messageBuilder(C2SCamelDashPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(C2SCamelDashPacket::encode)
                .decoder(C2SCamelDashPacket::new)
                .consumerMainThread(C2SCamelDashPacket::handle)
                .add();

        INSTANCE.messageBuilder(S2CUpdateCrownAdvancementsPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CUpdateCrownAdvancementsPacket::encode)
                .decoder(S2CUpdateCrownAdvancementsPacket::new)
                .consumerMainThread(S2CUpdateCrownAdvancementsPacket::handle)
                .add();

        INSTANCE.messageBuilder(S2CRevokeCrownAdvancementsPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CRevokeCrownAdvancementsPacket::encode)
                .decoder(S2CRevokeCrownAdvancementsPacket::new)
                .consumerMainThread(S2CRevokeCrownAdvancementsPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAllPlayer(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
}