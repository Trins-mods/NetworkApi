package trinsdar.networkapi.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import trinsdar.networkapi.impl.NetworkAPI;

public interface INetwork {
    static INetwork getInstance(){
        return NetworkAPI.getINSTANCE();
    }

    void sendToServer(ResourceLocation id, IPacket msg);

    void sendToClient(ResourceLocation id, IPacket msg, ServerPlayer player);

    default void sendToAll(ResourceLocation id, IPacket msg) {
        for (ServerPlayer player : getCurrentServer().getPlayerList().getPlayers()) {
            sendToClient(id, msg, player);
        }
    }

    default void sendToAll(ResourceLocation id, IPacket msg, ServerLevel dimension) {
        for (ServerPlayer player : getCurrentServer().getPlayerList().getPlayers()) {
            if (player.getLevel().equals(dimension)) {
                sendToClient(id, msg, player);
            }
        }
    }

    MinecraftServer getCurrentServer();

    default void sendToAllAround(ResourceLocation id, IPacket msg, ServerLevel world, AABB alignedBB) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, alignedBB)) {
            sendToClient(id, msg, player);
        }
    }
}
