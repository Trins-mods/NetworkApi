package trinsdar.networkapi.impl.forge;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import trinsdar.networkapi.api.INetwork;
import trinsdar.networkapi.api.IPacket;
import trinsdar.networkapi.api.PacketRegistration;
import trinsdar.networkapi.impl.NetworkAPI;

import java.util.Optional;
import java.util.function.Function;

@Mod("networkapi")
public class NetworkAPIImpl extends NetworkAPI {
    private static SimpleChannel handler;
    private static int currMessageId;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    public NetworkAPIImpl() {

        NetworkAPI.INSTANCE = new INetwork() {
            @Override
            public void sendToServer(ResourceLocation id, IPacket msg) {
                handler.sendToServer(msg);
            }

            @Override
            public void sendToClient(ResourceLocation id, IPacket msg, ServerPlayer player) {
                if (!(player instanceof FakePlayer))
                    handler.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }

            @Override
            public MinecraftServer getCurrentServer() {
                return ServerLifecycleHooks.getCurrentServer();
            }
        };
    }

    private static void initHandler(){
        if (handler == null){
            handler = NetworkRegistry.ChannelBuilder.
                    named(new ResourceLocation("networkapi", "main_channel")).
                    clientAcceptedVersions(PROTOCOL_VERSION::equals).
                    serverAcceptedVersions(PROTOCOL_VERSION::equals).
                    networkProtocolVersion(() -> PROTOCOL_VERSION).
                    simpleChannel();
        }
    }

    public static <MSG extends IPacket> void registerServerToClientPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        if (FMLEnvironment.dist.isDedicatedServer()) return;
        initHandler();
        handler.registerMessage(currMessageId++, clazz, IPacket::encode, decoder, (msg, ctx) ->{
            ctx.get().enqueueWork(msg::handleServer);
            ctx.get().setPacketHandled(true);
        }, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static  <MSG extends IPacket> void registerClientToServerPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        initHandler();
        handler.registerMessage(currMessageId++, clazz, IPacket::encode, decoder, (msg, ctx) ->{
            ctx.get().enqueueWork(() -> msg.handleClient(ctx.get().getSender()));
            ctx.get().setPacketHandled(true);
        }, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static  <MSG extends IPacket> void registerBothPacket(Class<MSG> clazz, ResourceLocation packetID, Function<FriendlyByteBuf, MSG> decoder){
        initHandler();
        handler.registerMessage(currMessageId++, clazz, IPacket::encode, decoder, (msg, ctx) ->{
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                    msg.handleClient(ctx.get().getSender());
                } else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT){
                    msg.handleServer();
                }
            });
            ctx.get().setPacketHandled(true);
        });
    }
}
