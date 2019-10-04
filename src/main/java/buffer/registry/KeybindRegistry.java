package buffer.registry;

import org.lwjgl.glfw.GLFW;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

/**
 * Clientside Keybind registry.
 */
public class KeybindRegistry {
    public static FabricKeyBinding BUFFER_SWITCH = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_switch"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, "Buffer").build();
    public static FabricKeyBinding BUFFER_PICKUP = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_pickup"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "Buffer").build();
    public static FabricKeyBinding BUFFER_VOID = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_void"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "Buffer").build();

    /**
     * Register all of Buffer's Keybinds and Keybind events.
     */
    public static void registerKeybinds() {
        /**
         * Register Buffer's Keybind category - currently broken!
         */
        KeyBindingRegistry.INSTANCE.addCategory("Buffer");

        /**
         * Register all of Buffer's Keybinds.
         */
        KeyBindingRegistry.INSTANCE.register(BUFFER_SWITCH);
        KeyBindingRegistry.INSTANCE.register(BUFFER_PICKUP);
        KeyBindingRegistry.INSTANCE.register(BUFFER_VOID);

        /**
         * Register all of Buffer's Keybind events.
         */
        ClientTickCallback.EVENT.register(event -> {
            /**
             * Register Buffer pickup mode Keybind.
             */
            if (BUFFER_PICKUP.isPressed() && BufferItem.pickupTick >= 2) {
                PlayerEntity playerEntity = MinecraftClient.getInstance().player;
                Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
                boolean isPickup = !playerEntity.getStackInHand(hand).getTag().getBoolean(BufferInventory.PICKUP_RETRIEVER);
                ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_PICKUP_PACKET, NetworkRegistry.createBufferPickupPacket(isPickup));
                BufferItem.pickupTick = 0;
            } else if (BUFFER_PICKUP.isPressed()) {
                ++BufferItem.pickupTick;
            }

            /**
             * Register Buffer void mode Keybind.
             */
            if (BUFFER_VOID.isPressed() && BufferItem.voidTick >= 2) {
                PlayerEntity playerEntity = MinecraftClient.getInstance().player;
                Hand hand = playerEntity.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM ? Hand.MAIN_HAND : Hand.OFF_HAND;
                boolean isVoid = !playerEntity.getStackInHand(hand).getTag().getBoolean(BufferInventory.VOID_RETRIEVER);
                ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_VOID_PACKET, NetworkRegistry.createBufferVoidPacket(isVoid));
                BufferItem.voidTick = 0;
            } else if (BUFFER_VOID.isPressed()) {
                ++BufferItem.voidTick;
            }

            /**
             * Register Buffer selection switch Keybind.
             */
            if (BUFFER_SWITCH.isPressed() && BufferItem.slotTick >= 2) {
                ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_SWITCH_PACKET, NetworkRegistry.createBufferSwitchPacket());
                BufferItem.slotTick = 0;
            } else if (BUFFER_SWITCH.isPressed()) {
                ++BufferItem.slotTick;
            }
        });
    }
}