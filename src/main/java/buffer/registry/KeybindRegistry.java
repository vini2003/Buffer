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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class KeybindRegistry {
    public static FabricKeyBinding BUFFER_SWITCH = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_switch"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, "Buffer").build();
    public static FabricKeyBinding BUFFER_PICKUP = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_pickup"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "Buffer").build();
    public static FabricKeyBinding BUFFER_VOID = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_void"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "Buffer").build();
    
    public static void registerKeybinds() {
        KeyBindingRegistry.INSTANCE.addCategory("Buffer");

        KeyBindingRegistry.INSTANCE.register(BUFFER_SWITCH);
        KeyBindingRegistry.INSTANCE.register(BUFFER_PICKUP);
        KeyBindingRegistry.INSTANCE.register(BUFFER_VOID);

        ClientTickCallback.EVENT.register(event -> {
            if (BUFFER_PICKUP.isPressed() && BufferItem.pickupTick >= 2) {
                BufferItem.pickupTick = 0;
                Hand hand = Hand.MAIN_HAND;
                if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (MinecraftClient.getInstance().player.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                ItemStack heldStack = MinecraftClient.getInstance().player.getStackInHand(hand);
                boolean isPickup = !heldStack.getTag().getBoolean(BufferInventory.PICKUP_RETRIEVER);
                ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_PICKUP_PACKET, NetworkRegistry.createBufferPickupPacket(isPickup));
            } else if (BUFFER_PICKUP.isPressed()) {
                ++BufferItem.pickupTick;
            }

            if (BUFFER_VOID.isPressed() && BufferItem.voidTick >= 2) {
                BufferItem.voidTick = 0;
                Hand hand = Hand.MAIN_HAND;
                if (MinecraftClient.getInstance().player.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.MAIN_HAND; } 
                else if (MinecraftClient.getInstance().player.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) { hand = Hand.OFF_HAND; }
                else { return; }
                ItemStack heldStack = MinecraftClient.getInstance().player.getStackInHand(hand);
                boolean isVoid = !heldStack.getTag().getBoolean(BufferInventory.VOID_RETRIEVER);
                ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_VOID_PACKET, NetworkRegistry.createBufferVoidPacket(isVoid));
            } else if (BUFFER_VOID.isPressed()) {
                ++BufferItem.voidTick;
            }

            if (BUFFER_SWITCH.isPressed() && BufferItem.slotTick >= 2) {
                BufferItem.slotTick = 0;
                MinecraftClient client = MinecraftClient.getInstance();
                PlayerEntity player = client.player;
                if (player.getMainHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                    ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_SWITCH_PACKET, NetworkRegistry.createBufferSwitchPacket(0));
                } else if (player.getOffHandStack().getItem() == ItemRegistry.BUFFER_ITEM) {
                    ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.BUFFER_SWITCH_PACKET, NetworkRegistry.createBufferSwitchPacket(1));
                }
            } else if (BUFFER_SWITCH.isPressed()) {
                ++BufferItem.slotTick;
            }
        });
    }
}