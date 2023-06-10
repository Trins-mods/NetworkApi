package trinsdar.networkapi.api;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import trinsdar.networkapi.impl.NetworkAPI;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class PacketRegistration {
    
    public static <MSG extends IPacket> void registerPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection direction){
        switch (direction){
            case PLAY_TO_CLIENT -> NetworkAPI.registerServerToClientPacket(clazz, packetID, decoder);
            case PLAY_TO_SERVER -> NetworkAPI.registerClientToServerPacket(clazz, packetID, decoder);
        }
    }

    public enum NetworkDirection{
        PLAY_TO_CLIENT, PLAY_TO_SERVER, LOGIN_TO_CLIENT, LOGIN_TO_SERVER;
    }
}
