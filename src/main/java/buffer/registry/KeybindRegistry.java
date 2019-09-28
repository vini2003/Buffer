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
    private static FabricKeyBinding BUFFER_SWITCH = FabricKeyBinding.Builder.create(new Identifier("buffer", "buffer_switch_0"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_PAGE_UP, "OwO who dis?").build();

    public static void registerKeybinds() {
        KeyBindingRegistry.INSTANCE.register(BUFFER_SWITCH);
        ClientTickCallback.EVENT.register(event -> {
            if (BUFFER_SWITCH.isPressed() && BufferItem.lockTick >= 5) {
                BufferItem.lockTick = 0;
                MinecraftClient.getInstance().getNetworkHandler().getConnection().send(NetworkRegistry.createStackSwitchPacket());
            } else if (BUFFER_SWITCH.isPressed()) {
                ++BufferItem.lockTick;
            }
        });
    }
}