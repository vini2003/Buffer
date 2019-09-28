package buffer.registry;

import org.lwjgl.glfw.GLFW;

import buffer.item.BufferItem;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
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
            if (BUFFER_PICKUP.isPressed() && BufferItem.pickupTick >= 5) {
                BufferItem.pickupTick = 0;
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createBufferPickupPacket());
            } else if (BUFFER_PICKUP.isPressed()) {
                ++BufferItem.pickupTick;
            }

            if (BUFFER_VOID.isPressed() && BufferItem.voidTick >= 5) {
                BufferItem.voidTick = 0;
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createBufferVoidPacket());
            } else if (BUFFER_VOID.isPressed()) {
                ++BufferItem.voidTick;
            }

            if (BUFFER_SWITCH.isPressed() && BufferItem.slotTick >= 5) {
                BufferItem.slotTick = 0;
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createStackSwitchPacket());
            } else if (BUFFER_SWITCH.isPressed()) {
                ++BufferItem.slotTick;
            }
        });
    }
}