package buffer.utility;

import buffer.registry.NetworkRegistry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BufferPacket {
    public static void sendPacket(ServerPlayerEntity playerEntity, int bufferSlot, int stackQuantity) {
        playerEntity.networkHandler.sendPacket(NetworkRegistry.createStackUpdatePacket(bufferSlot, stackQuantity));
    }
}