package buffer.registry;

import buffer.inventory.BufferInventory;
import buffer.screen.BufferItemController;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

// For this spaghetti, I blame akoimeex.

/**
 * Commonside Packet registry.
 */
public class NetworkRegistry {
	public static Identifier BUFFER_SWITCH_PACKET = new Identifier("buffer", "buffer_switch");
	public static Identifier BUFFER_PICKUP_PACKET = new Identifier("buffer", "buffer_pickup");
	public static Identifier BUFFER_VOID_PACKET = new Identifier("buffer", "buffer_void");

	/**
	 * Creates an empty PacketByteBuf for usage with Buffer selection switching.
	 *
	 * @return Empty PacketByteBuf.
	 */
	public static PacketByteBuf createBufferSwitchPacket() {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		return buffer;
	}

	/**
	 * Creates a custom PacketByteBuf for usage with Buffer pickup mode switching.
	 *
	 * @return Custom PacketByteBuf.
	 */
	public static PacketByteBuf createBufferPickupPacket(boolean isPickup) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(isPickup);
		return buffer;
	}

	/**
	 * Creates a custom PacketByteBuf for usage with Buffer void mode switching.
	 *
	 * @param isVoid Void mode boolean.
	 * @return Custom PacketByteBuf.
	 */
	public static PacketByteBuf createBufferVoidPacket(boolean isVoid) {
		PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
		buffer.writeBoolean(isVoid);
		return buffer;
	}

	/**
	 * Registers all custom packets used by Buffer.
	 */
	public static void registerPackets() {
		/**
		 * Register the Buffer selection switch packet.
		 */
		ServerSidePacketRegistry.INSTANCE.register(BUFFER_SWITCH_PACKET, (packetContext, packetByteBuffer) -> {
			packetContext.getTaskQueue().execute(() -> {
				PlayerEntity playerEntity = packetContext.getPlayer();
				Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
				BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
				bufferInventory.swapSlot();
				playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, new CompoundTag()));
			});
		});
		/**
		 * Register the Buffer pickup mode packet.
		 */
		ServerSidePacketRegistry.INSTANCE.register(BUFFER_PICKUP_PACKET, (packetContext, packetByteBuffer) -> {
			boolean isPickup = packetByteBuffer.readBoolean();
			packetContext.getTaskQueue().execute(() -> {
				PlayerEntity playerEntity = packetContext.getPlayer();
				Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
				if (playerEntity.container instanceof BufferItemController) {
					((BufferItemController) playerEntity.container).bufferInventory.isPickup = isPickup;
				}
				BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
				bufferInventory.isPickup = isPickup;
				playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, playerEntity.getStackInHand(hand).getTag()));
			});
		});
		/**
		 * Register the Buffer void mode packet.
		 */
		ServerSidePacketRegistry.INSTANCE.register(BUFFER_VOID_PACKET, (packetContext, packetByteBuffer) -> {
			boolean isVoid = packetByteBuffer.readBoolean();
			packetContext.getTaskQueue().execute(() -> {
				PlayerEntity playerEntity = packetContext.getPlayer();
				Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
				if (playerEntity.container instanceof BufferItemController) {
					((BufferItemController) playerEntity.container).bufferInventory.isVoid = isVoid;
				}
				BufferInventory bufferInventory = BufferInventory.fromTag(playerEntity.getStackInHand(hand).getTag());
				bufferInventory.isVoid = isVoid;
				playerEntity.getStackInHand(hand).setTag(BufferInventory.toTag(bufferInventory, playerEntity.getStackInHand(hand).getTag()));
			});
		});
	}
}
