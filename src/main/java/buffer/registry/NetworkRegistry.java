package buffer.registry;

import buffer.screen.BufferItemController;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class NetworkRegistry {
    public static Identifier BUFFER_UPDATE_PACKET = new Identifier("buffer", "buffer_update");

    public static CustomPayloadS2CPacket createStackUpdatePacket(Integer bufferSlot, Integer stackQuantity) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(bufferSlot);
        buffer.writeInt(stackQuantity);
        return new CustomPayloadS2CPacket(BUFFER_UPDATE_PACKET, buffer);
    }

    public static void registerPackets() {
        ClientSidePacketRegistry.INSTANCE.register(BUFFER_UPDATE_PACKET, (packetContext, packetByteBuffer) -> {
            Integer bufferSlot = packetByteBuffer.readInt();
            Integer packetStackQuantity = packetByteBuffer.readInt();
            if (packetStackQuantity != null && bufferSlot != null) {
                packetContext.getTaskQueue().execute(() -> {
                    PlayerEntity playerEntity = packetContext.getPlayer();
                    if (playerEntity.container instanceof BufferItemController) {
                        BufferItemController bufferContainer = (BufferItemController)playerEntity.container;
                        bufferContainer.bufferInventory.getSlot(bufferSlot).stackQuantity = packetStackQuantity;
                    }
                });   
            }
        });
    }
}