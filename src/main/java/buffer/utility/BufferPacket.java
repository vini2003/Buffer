package buffer.utility;

import buffer.registry.NetworkRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BufferPacket {
    public static void sendPacket(ServerPlayerEntity playerEntity, Integer bufferSlot, Integer stackQuantity) {
        playerEntity.networkHandler.sendPacket(NetworkRegistry.createStackUpdatePacket(bufferSlot, stackQuantity));
    }
}