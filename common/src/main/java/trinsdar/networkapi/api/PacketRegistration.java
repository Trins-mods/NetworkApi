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
    
    private static EnumMap<NetworkDirection, Map<Class<? extends IPacket>, Pair<ResourceLocation, Function<FriendlyByteBuf, ? extends IPacket>>>> REGISTRATION_MAP = new EnumMap<>(NetworkDirection.class);
    
    public static <MSG extends IPacket> void registerPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder, NetworkDirection direction){
        REGISTRATION_MAP.computeIfAbsent(direction, new Object2ObjectOpenHashMap<>()).put(clazz, Pair.of(packetID, decoder));
    }

    public enum NetworkDirection{
        PLAY_TO_CLIENT, PLAY_TO_SERVER, LOGIN_TO_CLIENT, LOGIN_TO_SERVER;
    }

    public static void registerPackets(){
        REGISTRATION_MAP.forEach((n, m) -> {
            switch (n) {
                case PLAY_TO_SERVER -> m.forEach((clazz, decoder) -> NetworkAPI.registerClientToServerPacket((Class<IPacket>)clazz, Pair.of(decoder.left(), (Function<FriendlyByteBuf, IPacket>) decoder.right())));
                case PLAY_TO_CLIENT -> m.forEach((clazz, decoder) -> NetworkAPI.registerServerToClientPacket((Class<IPacket>)clazz, Pair.of(decoder.left(), (Function<FriendlyByteBuf, IPacket>) decoder.right())));
                default -> {}
            }
        });
    }
}
