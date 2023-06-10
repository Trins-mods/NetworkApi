package trinsdar.networkapi.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/** Packet interface for your packet to implement.
 *  Use {@link} to send the packets and {@link } to register the packets
 */
public interface IPacket {
    /** Encodes packets into byte buffers
     * @param buf The byte buffer to encode info into.
     */
    void encode(FriendlyByteBuf buf);


    /** Handles packets sent from the client to the server.
    * @param sender The server player sending the packet.
     */
    void handleClient(ServerPlayer sender);

    /** Handles packets sent from the server to the client.
     */
    void handleServer();
}
