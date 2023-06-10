package trinsdar.networkapi.impl;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import trinsdar.networkapi.api.INetwork;
import trinsdar.networkapi.api.IPacket;

import java.util.function.Function;

public class NetworkAPI {
    protected static INetwork INSTANCE;

    public static INetwork getINSTANCE(){
        return INSTANCE;
    }

    @ExpectPlatform
    public static <MSG extends IPacket> void registerServerToClientPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <MSG extends IPacket> void registerClientToServerPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        throw new AssertionError();
    }
}
