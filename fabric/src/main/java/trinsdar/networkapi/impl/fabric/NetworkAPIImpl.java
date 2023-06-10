package trinsdar.networkapi.impl.fabric;

import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import trinsdar.networkapi.api.INetwork;
import trinsdar.networkapi.api.IPacket;
import trinsdar.networkapi.api.PacketRegistration;
import trinsdar.networkapi.impl.NetworkAPI;

import java.util.function.Function;

public class NetworkAPIImpl extends NetworkAPI implements ModInitializer {

    private static MinecraftServer currentServer;
    @Override
    public void onInitialize() {
        NetworkAPI.INSTANCE = new INetwork() {
            @Override
            public void sendToServer(ResourceLocation id, IPacket msg) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                msg.encode(buf);
                ClientPlayNetworking.send(id, buf);
            }

            @Override
            public void sendToClient(ResourceLocation id, IPacket msg, ServerPlayer player) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                msg.encode(buf);
                ServerPlayNetworking.send(player, id, buf);
            }

            @Override
            public MinecraftServer getCurrentServer() {
                return currentServer;
            }
        };
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
            currentServer = server;
        });
    }

    public static <MSG extends IPacket> void registerServerToClientPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        ClientPlayNetworking.registerGlobalReceiver(packetID, (client, handler, buf, responseSender) -> {
            IPacket packet = decoder.apply(buf);
            client.execute(packet::handleServer);
        });
    }

    public static <MSG extends IPacket> void registerClientToServerPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        ServerPlayNetworking.registerGlobalReceiver(packetID, (server, player, handler, buf, responseSender) -> {
            IPacket packet = decoder.apply(buf);
            server.execute(() -> packet.handleClient(player));
        });
    }
}
