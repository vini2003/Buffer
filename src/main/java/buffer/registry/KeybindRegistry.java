package buffer.registry;

import org.lwjgl.glfw.GLFW;

import buffer.inventory.BufferInventory;
import buffer.item.BufferItem;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class KeybindRegistry {
    private static FabricKeyBinding BUFFER_SWITCH = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_switch"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, "Buffer").build();
    private static FabricKeyBinding BUFFER_PICKUP = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_pickup"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_HOME, "Buffer").build();
    private static FabricKeyBinding BUFFER_VOID = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_void"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT, "Buffer").build();
    
    public static void registerKeybinds() {
        KeyBindingRegistry.INSTANCE.addCategory("Buffer");

        KeyBindingRegistry.INSTANCE.register(BUFFER_SWITCH);
        KeyBindingRegistry.INSTANCE.register(BUFFER_PICKUP);
        KeyBindingRegistry.INSTANCE.register(BUFFER_VOID);

        ClientTickCallback.EVENT.register(event -> {
            if (BUFFER_PICKUP.isPressed() && BufferItem.pickupTick >= 2) {
                BufferItem.pickupTick = 0;
                ItemStack heldStack = MinecraftClient.getInstance().player.inventory.getMainHandStack();
                Boolean isPickup = !heldStack.getTag().getBoolean(BufferInventory.PICKUP_RETRIEVER());
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createBufferPickupPacket(isPickup));
            } else if (BUFFER_PICKUP.isPressed()) {
                ++BufferItem.pickupTick;
            }

            if (BUFFER_VOID.isPressed() && BufferItem.voidTick >= 2) {
                BufferItem.voidTick = 0;
                ItemStack heldStack = MinecraftClient.getInstance().player.inventory.getMainHandStack();
                Boolean isVoid = !heldStack.getTag().getBoolean(BufferInventory.VOID_RETRIEVER());
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createBufferVoidPacket(isVoid));
            } else if (BUFFER_VOID.isPressed()) {
                ++BufferItem.voidTick;
            }

            if (BUFFER_SWITCH.isPressed() && BufferItem.slotTick >= 2) {
                BufferItem.slotTick = 0;
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createStackSwitchPacket());
            } else if (BUFFER_SWITCH.isPressed()) {
                ++BufferItem.slotTick;
            }
        });
    }
}