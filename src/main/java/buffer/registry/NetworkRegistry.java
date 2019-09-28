package buffer.registry;

import buffer.inventory.BufferInventory;
import buffer.screen.BufferEntityController;
import buffer.screen.BufferItemController;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class NetworkRegistry {
    public static Identifier BUFFER_UPDATE_PACKET = new Identifier("buffer", "buffer_update");
    public static Identifier BUFFER_SWITCH_PACKET = new Identifier("buffer", "buffer_switch");
    public static Identifier BUFFER_PICKUP_PACKET = new Identifier("buffer", "buffer_pickup");
    public static Identifier BUFFER_VOID_PACKET = new Identifier("buffer", "buffer_void");

    public static CustomPayloadS2CPacket createStackUpdatePacket(Integer bufferSlot, Integer stackQuantity) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(bufferSlot);
        buffer.writeInt(stackQuantity);
        return new CustomPayloadS2CPacket(BUFFER_UPDATE_PACKET, buffer);
    }

    public static CustomPayloadC2SPacket createStackSwitchPacket() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return new CustomPayloadC2SPacket(BUFFER_SWITCH_PACKET, buffer);
    }

    public static CustomPayloadC2SPacket createBufferPickupPacket() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return new CustomPayloadC2SPacket(BUFFER_PICKUP_PACKET, buffer);
    }

    public static CustomPayloadC2SPacket createBufferVoidPacket() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        return new CustomPayloadC2SPacket(BUFFER_VOID_PACKET, buffer);
    }

    public static void registerPackets() {
        ClientSidePacketRegistry.INSTANCE.register(BUFFER_UPDATE_PACKET, (packetContext, packetByteBuffer) -> {
            Integer bufferSlot = packetByteBuffer.readInt();
            Integer packetStackQuantity = packetByteBuffer.readInt();
            if (packetStackQuantity != null && bufferSlot != null) {
                packetContext.getTaskQueue().execute(() -> {
                    PlayerEntity playerEntity = packetContext.getPlayer();
                    if (playerEntity.container instanceof BufferItemController) {
                        BufferItemController bufferController = (BufferItemController)playerEntity.container;
                        if (bufferController.bufferInventory != null) {
                            bufferController.bufferInventory.getSlot(bufferSlot).stackQuantity = packetStackQuantity;
                        }
                    }
                    if (playerEntity.container instanceof BufferEntityController) {
                        BufferEntityController bufferController = (BufferEntityController)playerEntity.container;
                        if (bufferController.bufferInventory != null) {
                            bufferController.bufferInventory.getSlot(bufferSlot).stackQuantity = packetStackQuantity;
                        }
                    }
                });   
            }
        });
        ServerSidePacketRegistry.INSTANCE.register(BUFFER_SWITCH_PACKET, (packetContext, packetByteBuffer) -> {
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                if (playerEntity.inventory.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                    BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.inventory.getMainHandStack().getTag());
                    bufferInventory.swapSlot();
                    playerEntity.getMainHandStack().setTag(BufferInventory.toTag(bufferInventory, playerEntity.inventory.getMainHandStack().getTag()));
                }
            });   
        });

        ServerSidePacketRegistry.INSTANCE.register(BUFFER_PICKUP_PACKET, (packetContext, packetByteBuffer) -> {
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                if (playerEntity.inventory.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                    BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.inventory.getMainHandStack().getTag());
                    bufferInventory.swapPickup();
                    playerEntity.getMainHandStack().setTag(BufferInventory.toTag(bufferInventory, playerEntity.inventory.getMainHandStack().getTag()));
                }
            });   
        });

        ServerSidePacketRegistry.INSTANCE.register(BUFFER_VOID_PACKET, (packetContext, packetByteBuffer) -> {
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                if (playerEntity.inventory.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                    BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.inventory.getMainHandStack().getTag());
                    bufferInventory.swapVoid();
                    playerEntity.getMainHandStack().setTag(BufferInventory.toTag(bufferInventory, playerEntity.inventory.getMainHandStack().getTag()));
                }
            });   
        });
    }
}