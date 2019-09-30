package buffer.registry;

import buffer.inventory.BufferInventory;
import buffer.screen.BufferEntityController;
import buffer.screen.BufferItemController;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

// For this spaghetti, I blame akoimeex.
public class NetworkRegistry {
    public static Identifier BUFFER_UPDATE_PACKET = new Identifier("buffer", "buffer_update");
    public static Identifier BUFFER_SWITCH_PACKET = new Identifier("buffer", "buffer_switch");
    public static Identifier BUFFER_PICKUP_PACKET = new Identifier("buffer", "buffer_pickup");
    public static Identifier BUFFER_VOID_PACKET = new Identifier("buffer", "buffer_void");
    public static Identifier BUFFER_PICKUP_RETURN_PACKET = new Identifier("buffer", "buffer_pickup_return");
    public static Identifier BUFFER_VOID_RETURN_PACKET = new Identifier("buffer", "buffer_void_return");

    public static PacketByteBuf createStackUpdatePacket(int bufferSlot, int stackQuantity) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(bufferSlot);
        buffer.writeInt(stackQuantity);
        return buffer;
    }

    public static PacketByteBuf createBufferSwitchPacket(int hand) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeInt(hand);
        return buffer;
    }

    public static PacketByteBuf createBufferPickupPacket(boolean isPickup) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBoolean(isPickup);
        return buffer;
    }

    public static PacketByteBuf createBufferPickupReturnPacket(boolean isPickup) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBoolean(isPickup);
        return buffer;
    }

    public static PacketByteBuf createBufferVoidPacket(boolean isVoid) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBoolean(isVoid);
        return buffer;
    }

    public static PacketByteBuf createBufferVoidReturnPacket(boolean isVoid) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeBoolean(isVoid);
        return buffer;
    }

    public static void registerPackets() {
        ClientSidePacketRegistry.INSTANCE.register(BUFFER_UPDATE_PACKET, (packetContext, packetByteBuffer) -> {
            int bufferSlot = packetByteBuffer.readInt();
            int packetStackQuantity = packetByteBuffer.readInt();
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
        });

        ServerSidePacketRegistry.INSTANCE.register(BUFFER_SWITCH_PACKET, (packetContext, packetByteBuffer) -> {
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                Hand hand = Hand.MAIN_HAND;
                if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                if (playerEntity.getStackInHand(hand).getItem() == ItemRegistry.BUFFER_ITEM) {
                    BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
                    bufferInventory.swapSlot();
                    playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
                }
            });
        });
        ServerSidePacketRegistry.INSTANCE.register(BUFFER_PICKUP_PACKET, (packetContext, packetByteBuffer) -> {
            boolean isPickup = packetByteBuffer.readBoolean();
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                Hand hand = Hand.MAIN_HAND;
                if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                if (playerEntity.container instanceof BufferItemController) {
                    BufferItemController controller = ((BufferItemController)playerEntity.container);
                    controller.bufferInventory.isPickup = isPickup;
                }
                BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
                bufferInventory.isPickup = isPickup;
                playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, playerEntity.getStackInHand(hand).getTag()));
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(BUFFER_VOID_PACKET, (packetContext, packetByteBuffer) -> {
            boolean isVoid = packetByteBuffer.readBoolean();
            packetContext.getTaskQueue().execute(() -> {
                    PlayerEntity playerEntity = packetContext.getPlayer();
                    Hand hand = Hand.MAIN_HAND;
                    if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                    else if (playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                    else { return; }
                    if (playerEntity.container instanceof BufferItemController) {
                        BufferItemController controller = ((BufferItemController)playerEntity.container);
                        controller.bufferInventory.isVoid = isVoid;
                    }
                    BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
                    bufferInventory.isVoid = isVoid;
                    playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, playerEntity.getStackInHand(hand).getTag()));
                    System.out.println("Server: " + bufferInventory.isVoid);
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(BUFFER_PICKUP_RETURN_PACKET, (packetContext, packetByteBuffer) -> {
            boolean isPickup = packetByteBuffer.readBoolean();
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                Hand hand = Hand.MAIN_HAND;
                if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                if (playerEntity.getStackInHand(hand).getItem() == ItemRegistry.BUFFER_ITEM) {
                    playerEntity.getStackInHand(hand).getTag().putBoolean(BufferInventory.PICKUP_RETRIEVER, isPickup);
                }
                ((BufferItemController)playerEntity.container).bufferInventory.isPickup = isPickup;
                CompoundTag itemTag = playerEntity.getStackInHand(hand).getTag();
                itemTag = BufferInventory.toTag(((BufferItemController)playerEntity.container).bufferInventory, new CompoundTag());
                playerEntity.getStackInHand(hand).setTag(itemTag);
                playerEntity.setStackInHand(hand, playerEntity.getStackInHand(hand));
                playerEntity.inventory.updateItems();
                playerEntity.inventory.markDirty();
            });
        });

        ClientSidePacketRegistry.INSTANCE.register(BUFFER_VOID_RETURN_PACKET, (packetContext, packetByteBuffer) -> {
            boolean isVoid = packetByteBuffer.readBoolean();
            packetContext.getTaskQueue().execute(() -> {
                PlayerEntity playerEntity = packetContext.getPlayer();
                Hand hand = Hand.MAIN_HAND;
                if (playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (playerEntity.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                if (playerEntity.getStackInHand(hand).getItem() == ItemRegistry.BUFFER_ITEM) {
                    playerEntity.getStackInHand(hand).getTag().putBoolean(BufferInventory.VOID_RETRIEVER, isVoid);
                }
                ((BufferItemController)playerEntity.container).bufferInventory.isVoid = isVoid;
                playerEntity.inventory.updateItems();
                playerEntity.inventory.markDirty();
            });   
        });

    }
}
